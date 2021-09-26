package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.dblpinfo.model.ModelSamples
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.dblpinfo.tranformation.dynamic.{ICMTActiveAuthors, ICMTAuthors, InactiveICMTButActiveAuthors, JournalISTActiveAuthors}
import org.atlanmod.transformation.parallel.TransformationEngineTwoPhaseByRule

object MainDBLP {

    final val DEFAULT_NCORE: Int = 1
    var ncore: Int = DEFAULT_NCORE

    final val DEFAULT_NSTEP: Int = 5
    var nstep: Int = DEFAULT_NSTEP

    final val DEFAULT_SIZE: Int = 700
    var size: Int = DEFAULT_SIZE

    final val DEFAULT_NEXECUTOR: Int = 1
    var nexecutor: Int = DEFAULT_NEXECUTOR

    final val DEFAULT_NPARTITION: Int = -1
    var npartition: Int = DEFAULT_NPARTITION

    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-core" :: core :: args => {
                ncore = core.toInt
                parseArgs(args)
            }
            case "-executor" :: executor :: args =>{
                nexecutor = executor.toInt
                parseArgs(args)
            }
            case "-partition" :: partition :: args =>{
                npartition = partition.toInt
                parseArgs(args)
            }
            case "-step" :: step :: args => {
                nstep = step.toInt
                parseArgs(args)
            }
            case "-size" :: s :: args => {
                size = s.toInt
                parseArgs(args)
            }
            case _ :: args => parseArgs(args)
            case List() => if (npartition == DEFAULT_NPARTITION) npartition = ncore * nexecutor
        }
    }

    def getContext(): SparkContext = {
        val conf = new SparkConf()
        new SparkContext(conf)
    }

    def main(args: Array[String]): Unit = {
        parseArgs(args.toList)
        val sc = getContext()

        val input_model: DblpModel = ModelSamples.getReplicatedSimple(size)
        val input_metamodel = DblpMetamodel.metamodel

        val q1 = ICMTAuthors.find
        val q2 = ICMTActiveAuthors.find
        val q3 = InactiveICMTButActiveAuthors.find
        val q4 = JournalISTActiveAuthors.find

        val line_1 = List("q1",input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
        val line_2 = List("q2",input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
        val line_3 = List("q3",input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
        val line_4 = List("q4",input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")

        val res_1 = TransformationEngineTwoPhaseByRule.execute_bystep(q1, input_model, input_metamodel, npartition, sc, nstep)
        val res_2 = TransformationEngineTwoPhaseByRule.execute_bystep(q2, input_model, input_metamodel, npartition, sc, nstep)
        val res_3 = TransformationEngineTwoPhaseByRule.execute_bystep(q3, input_model, input_metamodel, npartition, sc, nstep)
        val res_4 = TransformationEngineTwoPhaseByRule.execute_bystep(q4, input_model, input_metamodel, npartition, sc, nstep)

        println(line_1 + "," + res_1._1 + "," + res_1._2.mkString(","))
        println(line_2 + "," + res_2._1 + "," + res_2._2.mkString(","))
        println(line_3 + "," + res_3._1 + "," + res_3._2.mkString(","))
        println(line_4 + "," + res_4._1 + "," + res_4._2.mkString(","))
    }
}
