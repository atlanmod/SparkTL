package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.findcouples.model.movie.{MovieXMItoJSON, MovieMetamodel}
import org.atlanmod.transformation.parallel.TransformationEngineTwoPhaseByRule

object Main_IMDB {

    final val DEFAULT_NCORE: Int = 1
    var ncore: Int = DEFAULT_NCORE

    final val DEFAULT_NSTEP: Int = 5
    var nstep: Int = DEFAULT_NSTEP

    final val DEFAULT_NEXECUTOR: Int = 1
    var nexecutor: Int = DEFAULT_NEXECUTOR

    final val DEFAULT_NPARTITION: Int = -1
    var npartition: Int = DEFAULT_NPARTITION

    var ecore: String = ""
    var xmi: String = ""

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
            case "-step" :: step :: args =>{
                nstep = step.toInt
                parseArgs(args)
            }
            case "-xmi" :: file :: args =>{
                xmi = file
                parseArgs(args)
            }
            case "-ecore" :: file :: args =>{
                ecore = file
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
        println("RUNNING...")
       try {
           parseArgs(args.toList)
           println("PARSED ARGS: OK")
//           xmi = "/home/jolan/Scala/SparkTL/SparkTL/deployment/g5k/imdb-0.1.xmi"
//           ecore = "/home/jolan/Scala/SparkTL/SparkTL/deployment/g5k/movies.ecore"
           val sc = getContext()
           println("SPARK CONTEXT: OK")
           val transformation = org.atlanmod.findcouples.transformation.dynamic.FindCouples.findcouples_imdb
           println("LOAD TRANSFORMATION: OK ")
           val input_model = MovieXMItoJSON.load(xmi, ecore)
           println("LOAD MODEL: OK")
           val input_metamodel = MovieMetamodel.metamodel
           println("LOAD METAMODEL: OK")
           var line = List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
           val res = TransformationEngineTwoPhaseByRule.execute_bystep(transformation, input_model, input_metamodel, npartition, sc, nstep)
           line = List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
           println(line + "," + res._1 + "," + res._2.mkString(","))

       } catch {
           case e: Exception => println(e.getMessage)
       }
    }
}
