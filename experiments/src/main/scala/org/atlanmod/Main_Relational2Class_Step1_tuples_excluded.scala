package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.relationalmodel.RelationalMetamodel
import org.atlanmod.transformation.parallel.TransformationEngineOnePhase
import org.atlanmod.util.R2CUtil

object Main_Relational2Class_Step1_tuples_excluded {
    final val DEFAULT_NCORE: Int = 1
    final val DEFAULT_NEXECUTOR: Int = 2
    final val DEFAULT_NPARTITION: Int = 4
    final val DEFAULT_SIZE: Int = 10
    final val DEFAULT_SLEEPING_GUARD: Int = 0
    final val DEFAULT_SLEEPING_INSTANTIATE: Int = 0
    final val DEFAULT_SLEEPING_APPLY: Int = 0

    var ncore: Int = DEFAULT_NCORE
    var nexecutor: Int = DEFAULT_NEXECUTOR
    var model_size: Int = DEFAULT_SIZE
    var npartition: Int = DEFAULT_NPARTITION
    var sleeping_guard: Int = DEFAULT_SLEEPING_GUARD
    var sleeping_instantiate: Int = DEFAULT_SLEEPING_INSTANTIATE
    var sleeping_apply: Int = DEFAULT_SLEEPING_APPLY

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
            case "-sleep_guard" :: sleep :: args => {
                sleeping_guard = sleep.toInt
                parseArgs(args)
            }
            case "-sleep_instantiate" :: sleep :: args => {
                sleeping_instantiate = sleep.toInt
                parseArgs(args)
            }
            case "-sleep_apply" :: sleep :: args => {
                sleeping_apply = sleep.toInt
                parseArgs(args)
            }
            case "-executor" :: executor :: args =>{
                nexecutor = executor.toInt
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
        val sc = new SparkContext(conf)

        val transformation = org.atlanmod.class2relational.transformation.dynamic.Relational2Class.relational2class(sleeping_guard, sleeping_instantiate, sleeping_apply)

        val input_model = R2CUtil.get_model_from_n_patterns(model_size)
        val input_metamodel = RelationalMetamodel.metamodel

        val res: (Double, List[Double]) =
            TransformationEngineOnePhase.execute_with_collect_tuples(transformation, input_model, input_metamodel, npartition, sc)

        val a_line =
            List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
        println(a_line + "," + res._1 + "," + res._2.mkString(","))
    }

}
