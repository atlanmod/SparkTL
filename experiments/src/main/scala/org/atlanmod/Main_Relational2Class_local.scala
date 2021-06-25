package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.relationalmodel.RelationalMetamodel
import org.atlanmod.transformation.parallel.TransformationEngineTwoPhase
import org.atlanmod.util.R2CUtil

object Main_Relational2Class_local {
    final val DEFAULT_NCORE: Int = 1
    final val DEFAULT_NPARTITION: Int = 4
    final val DEFAULT_PATTERN: Int = 1
    final val DEFAULT_MODE: String = "dumb"
    final val DEFAULT_SLEEPING: Int = 1

    var ncore: Int = DEFAULT_NCORE
    var model_times_pattern: Int = DEFAULT_PATTERN
    var npartition: Int = DEFAULT_NPARTITION
    var execution_mode: String = DEFAULT_MODE
    var sleeping: Int = DEFAULT_SLEEPING

    def main(args: Array[String]): Unit = {
        npartition =  ncore * 4
        val conf = new SparkConf()
        conf.setMaster("local["+ncore+"]")
        conf.setAppName("test")
        val sc = new SparkContext(conf)

        var transformation = org.atlanmod.class2relational.transformation.dynamic.Relational2Class.relational2class_simple()
        if (execution_mode.equals("dumb"))
            transformation =  org.atlanmod.class2relational.transformation.dynamic.Relational2Class.relational2class_sleeping_instantiate_and_apply(sleeping)

        val input_model = R2CUtil.get_model_from_n_patterns(model_times_pattern)
        val input_metamodel = RelationalMetamodel.metamodel

        val res: (Double, List[Double]) =
            TransformationEngineTwoPhase.execute(transformation, input_model, input_metamodel, npartition, sc)

        val a_line =
            List(input_model.allModelElements.length, input_model.allModelLinks.length, ncore, npartition).mkString(",")
        println("nb_element,nb_links,nb_core,nb_partition,total_time,time_step1,time_bcast,time_step2")
        println(a_line + "," + res._1 + "," + res._2.mkString(","))
    }

}
