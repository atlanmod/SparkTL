package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.relationalmodel.RelationalMetamodel
import org.atlanmod.transformation.parallel.TransformationEngineTwoPhase
import org.atlanmod.util.R2CUtil

object Main_Relational2Class_local {
    final val DEFAULT_NCORE: Int = 1
    final val DEFAULT_NPARTITION: Int = 4
    final val DEFAULT_SIZE: Int = 1
    final val DEFAULT_SLEEPING_GUARD: Int = 0
    final val DEFAULT_SLEEPING_INSTANTIATE: Int = 0
    final val DEFAULT_SLEEPING_APPLY: Int = 0

    var ncore: Int = DEFAULT_NCORE
    var model_size: Int = DEFAULT_SIZE
    var npartition: Int = DEFAULT_NPARTITION
    var sleeping_guard: Int = DEFAULT_SLEEPING_GUARD
    var sleeping_instantiate: Int = DEFAULT_SLEEPING_INSTANTIATE
    var sleeping_apply: Int = DEFAULT_SLEEPING_APPLY

    def main(args: Array[String]): Unit = {
        npartition =  ncore * 4
        val conf = new SparkConf()
        conf.setMaster("local["+ncore+"]")
        conf.setAppName("test")
        val sc = new SparkContext(conf)

        val transformation = org.atlanmod.class2relational.transformation.dynamic.Relational2Class.relational2class(sleeping_guard, sleeping_instantiate, sleeping_apply)

        val input_model = R2CUtil.get_model_from_n_patterns(model_size)
        val input_metamodel = RelationalMetamodel.metamodel

        val res: (Double, List[Double]) =
            TransformationEngineTwoPhase.execute(transformation, input_model, input_metamodel, npartition, sc)

        val a_line =
            List(input_model.allModelElements.length, input_model.allModelLinks.length, ncore, npartition).mkString(",")
        println("nb_element,nb_links,nb_core,nb_partition,total_time,time_step1,time_bcast,time_step2")
        println(a_line + "," + res._1 + "," + res._2.mkString(","))
    }

}
