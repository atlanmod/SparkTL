package org.atlanmod

import java.io.{File, PrintWriter}
import java.util.Calendar

import org.apache.spark.SparkContext
import org.atlanmod.model.dynamic.classModel._
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.SparkUtil
import org.atlanmod.transformation.dynamic.Class2Relational

object Util {

    final val DIR_RES_NAME = "results"
    final val FILE_RES_EXT = "r"
    final val FILE_RES_NAME = "results"

    private def get_local_time: String = {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val hour = now.get(Calendar.HOUR)
        val minute = now.get(Calendar.MINUTE)
        val second = now.get(Calendar.SECOND)

        year + ":" + month + ":" + day + "_" + hour + ":" + minute + ":" + second
    }

    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1): ClassModel = {
        var elements: List[ClassElement] = List()
        var links: List[ClassLink] = List()
        for (i <- 1 to nclass) {
            val cc = new ClassClass(i.toString, "")
            elements = cc :: elements
            var cc_attributes: List[ClassAttribute] = List()
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

    private def write_content(filename: String, content: String): Unit = {
        val pw = new PrintWriter(new File(filename))
        pw.write(content)
        pw.close()
    }

    private def create_if_not_exits(dirname: String): Unit = {
        val dir: File = new File(dirname)
        if (!dir.exists) dir.mkdirs()
    }

    def main(args: Array[String]): Unit = {
        val max_ten = 2
        val ntests = 30
        var total_vector = ""
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.transformation()
        val sc = SparkUtil.context()

        val tests:
            List[(String,
              (Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink],
                Model[DynamicElement, DynamicLink],
                Metamodel[DynamicElement, DynamicLink, String, String], SparkContext)
                => Model[DynamicElement, DynamicLink])
            ]
        =
            List(
                ("seq.simple", (tr, m, mm, sc) => org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("par.simple", (tr, m, mm, sc) => org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("seq.byrule", (tr, m, mm, sc) => org.atlanmod.tl.engine.parallel.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("par.byrule", (tr, m, mm, sc) => org.atlanmod.tl.engine.sequential.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("seq.twophase", (tr, m, mm, sc) => org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
                ("par.twophase", (tr, m, mm, sc) => org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
            )

        for (i <- 1 to max_ten) {
            val size = Math.pow(10, i).toInt
            val model = dynamic_simple_model(size, size)
            for (test <- tests) {
                val name_test = test._1
                val foo_test = test._2
                var res_vector = name_test + "." + size + " <- c("
                for (n <- 1 to ntests) {
                    val t1 = System.nanoTime
                    foo_test(transformation, model, metamodel, sc)
                    val computation_time = (System.nanoTime - t1) * 1000 / 1e9d
                    res_vector = res_vector + computation_time + (if (n == ntests) ")\n" else ",")
                    total_vector = total_vector + res_vector
                }
                create_if_not_exits(DIR_RES_NAME)
                val filename = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + get_local_time + "." + name_test + "_" + size + "." + FILE_RES_EXT
                write_content(filename, res_vector)
            }
        }

        create_if_not_exits(DIR_RES_NAME)
        val filename = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + get_local_time + "." + FILE_RES_EXT
        write_content(filename, total_vector)

    }
}
