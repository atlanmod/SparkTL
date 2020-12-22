package org.atlanmod.transformation

import org.apache.spark.SparkContext
import org.atlanmod.model.dynamic.classModel._
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.{Model, Transformation}
import org.atlanmod.tl.util.SparkUtil
import org.atlanmod.util.{FileUtil, RUtil, TimeUtil}

object Class2Relational{

    type SME = DynamicElement
    type SML = DynamicLink
    type SMC = String
    type SMR = String
    type TME = DynamicElement
    type TML = DynamicLink

    type transformation_type = Transformation[SME, SML, SMC, TME, TML]
    type source_model = ClassModel
    type source_metamodel = DynamicMetamodel[DynamicElement, DynamicLink]
    type target_model = Model[DynamicElement, DynamicLink]

    type transformation_function = (transformation_type, source_model, source_metamodel, SparkContext) => target_model

    final val DIR_RES_NAME = "c2r_results"
    final val FILE_RES_EXT = "r"
    final val FILE_RES_NAME = "results"

    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1): ClassModel = {
        var elements : List[ClassElement] = List()
        var links : List[ClassLink] = List()
        for(i <- 1 to nclass){
            val cc = new ClassClass(i.toString, "")
            elements = cc :: elements
            var cc_attributes : List[ClassAttribute] = List()
            for (j <- 1 to nattribute) {
                val ca = new ClassAttribute(i.toString + "." + j.toString, "")
                elements = ca :: elements
                links = new AttributeToClass(ca, cc) :: links
                cc_attributes = ca :: cc_attributes
            }
            links = new ClassToAttributes(cc, cc_attributes) :: links

        }
        new ClassModel(elements, links)
    }

    def run_test(tr_foo: transformation_function, tr: transformation_type,
                 sm: source_model, mm: source_metamodel, sc: SparkContext): Double = {
        val t1 = System.nanoTime
        tr_foo(tr, sm, mm, sc)
        (System.nanoTime - t1) * 1000 / 1e9d
    }

    def run_tests(tr_foo: transformation_function, tr: transformation_type,
                 sm: source_model, mm: source_metamodel, sc: SparkContext, times: Int): List[Double] = {
        var res: List[Double] = List()
        for(i <- 1 to times) {
            res = run_test(tr_foo, tr, sm, mm, sc) :: res
        }
        res
    }

    def get_tests(): List[(String, transformation_function)] = {
        val res : List[(String, transformation_function)] =
            List(
                ("seq.simple", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("par.simple", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("seq.byrule", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.parallel.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("par.byrule", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.sequential.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("seq.twophase", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
                ("par.twophase", (tr, m, mm, sc) =>  org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
            )
        res
    }

    def main(args: Array[String]) : Unit = {
        FileUtil.create_if_not_exits(DIR_RES_NAME)

        val max_ten = 10
        val ntests = 30

        var r_file_content_global = ""

        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.transformation()
        val sc = SparkUtil.context

        for(i <- 0 to max_ten){
            val size = Math.pow(10, i).toInt
            val model = dynamic_simple_model(size, size)

            var r_file_content_current = ""

            for (test <- get_tests()){
                val name_test = test._1
                val foo_test = test._2
                val list_of_times = run_tests(foo_test, transformation, model, metamodel, sc, ntests)
                val vector = RUtil.r_vector(name = name_test + "." + size, times = list_of_times)
                println(vector)
                r_file_content_current += vector + "\n"
                r_file_content_global += vector + "\n"
            }
            val filename_current = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + TimeUtil.strLocalTime + "_" + size + "." + FILE_RES_EXT
            FileUtil.write_content(filename_current, r_file_content_current)
        }

        val filename_global = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + TimeUtil.strLocalTime  + "." + FILE_RES_EXT
        FileUtil.write_content( filename_global, r_file_content_global)
    }
}