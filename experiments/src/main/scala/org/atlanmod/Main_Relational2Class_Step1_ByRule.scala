package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.model.relationalmodel.RelationalMetamodel
import org.atlanmod.transformation.parallel.TransformationEngineOnePhaseByRule
import org.atlanmod.util.R2CUtil

object Main_Relational2Class_Step1_ByRule {
    final val DEFAULT_NCORE: Int = 1
    final val DEFAULT_NEXECUTOR: Int = 2
    final val DEFAULT_NPARTITION: Int = 4
    final val DEFAULT_SIZE: Int = 10
    final val DEFAULT_MODE: String = "dumb"
    final val DEFAULT_SLEEPING: Int = 1

    var ncore: Int = DEFAULT_NCORE
    var nexecutor: Int = DEFAULT_NEXECUTOR
    var model_size: Int = DEFAULT_SIZE
    var npartition: Int = DEFAULT_NPARTITION
    var execution_mode: String = DEFAULT_MODE
    var sleeping: Int = DEFAULT_SLEEPING

    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-size" :: size :: args => {
                model_size = size.toInt
                parseArgs(args)
            }
            case "-core" :: core :: args => {
                ncore = core.toInt
                parseArgs(args)
            }
            case "-sleep" :: sleep :: args => {
                sleeping = sleep.toInt
                parseArgs(args)
            }
            case "-executor" :: executor :: args =>{
                nexecutor = executor.toInt
                parseArgs(args)
            }
            case "-mode" :: mode :: args => {
                assert(mode.equals("dumb") || mode.equals("simple") || mode.equals("super_dumb"))
                execution_mode = mode
                parseArgs(args)
            }
            case _ :: args => parseArgs(args)
            case List() =>
        }
    }

    def main(args: Array[String]): Unit = {
        parseArgs(args.toList)
        npartition =  ncore * nexecutor * 4
        val conf = new SparkConf()
        // conf.setAppName("name")
        // conf.setMaster("local")
        val sc = new SparkContext(conf)

        var transformation = org.atlanmod.transformation.dynamic.Relational2Class.relational2class_simple()
        if (execution_mode.equals("dumb"))
            transformation =  org.atlanmod.transformation.dynamic.Relational2Class.relational2class_sleeping_instanciate_and_apply(sleeping)
        if (execution_mode.equals("super_dumb"))
            transformation =  org.atlanmod.transformation.dynamic.Relational2Class.relational2class_sleeping_from_instanciate_apply(sleeping)

        val input_model = R2CUtil.get_model_from_n_patterns(model_size)
        val input_metamodel = RelationalMetamodel.metamodel

        val res: (Double, List[Double]) =
            TransformationEngineOnePhaseByRule.execute(transformation, input_model, input_metamodel, npartition, sc)

        val a_line =
            List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
        println(a_line + "," + res._1 + "," + res._2.mkString(","))
    }

}
