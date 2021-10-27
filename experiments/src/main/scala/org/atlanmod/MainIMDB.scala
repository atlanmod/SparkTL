package org.atlanmod

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.findcouples.model.movie.{MovieJSONLoader, MovieMetamodel, MovieModel}
import org.atlanmod.transformation.parallel.TransformationEngineTwoPhaseByRule

object MainIMDB {

    final val DEFAULT_NCORE: Int = 1
    var ncore: Int = DEFAULT_NCORE

    final val DEFAULT_NSTEP: Int = 5
    var nstep: Int = DEFAULT_NSTEP

    final val DEFAULT_NEXECUTOR: Int = 1
    var nexecutor: Int = DEFAULT_NEXECUTOR

    final val DEFAULT_NPARTITION: Int = -1
    var npartition: Int = DEFAULT_NPARTITION

    final val DEFAULT_STORAGE: StorageLevel = StorageLevel.MEMORY_AND_DISK
    var storage:  StorageLevel = DEFAULT_STORAGE
    final val DEFAULT_STORAGE_STRING: String = "MEMORY_AND_DISK"
    var storage_string:  String = DEFAULT_STORAGE_STRING

//    var json_actors: String = "/home/jolan/Scala/SparkTL/deployment/g5k/actors_imdb-0.1.json"
//    var json_movies: String = "/home/jolan/Scala/SparkTL/deployment/g5k/movies_imdb-0.1.json"
//    var txt_links: String = "/home/jolan/Scala/SparkTL/deployment/g5k/links_imdb-0.1.txt"
    var json_actors: String = "deployment/g5k/actors_sw.json"
    var json_movies: String = "deployment/g5k/movies_sw.json"
    var txt_links: String = "deployment/g5k/links_sw.txt"

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
            case "-actors" :: file :: args =>{
                json_actors = file
                parseArgs(args)
            }
            case "-movies" :: file :: args =>{
                json_movies = file
                parseArgs(args)
            }
            case "-links" :: file :: args =>{
                txt_links = file
                parseArgs(args)
            }
            case "-persist" :: level :: args =>{
                storage = StorageLevel.fromString(level)
                storage_string = level
                parseArgs(args)
            }
            case _ :: args => parseArgs(args)
            case List() => if (npartition == DEFAULT_NPARTITION) npartition = ncore * nexecutor * 4
        }
    }

    def getContext(): SparkContext = {
        val conf = new SparkConf()
//        conf.setMaster("local")
//        conf.setAppName("Test")
        new SparkContext(conf)
    }

    def main(args: Array[String]): Unit = {
//       try {
           parseArgs(args.toList)
           val sc = getContext()
           val transformation = org.atlanmod.findcouples.transformation.dynamic.FindCouples.findcouples_imdb
           val input_model : MovieModel = MovieJSONLoader.load(json_actors,json_movies,txt_links)
           val input_metamodel = MovieMetamodel.metamodel
           var line = List(storage_string,input_model.numberOfElements, input_model.numberOfLinks, nexecutor, ncore, npartition).mkString(",")
           val res = TransformationEngineTwoPhaseByRule.execute_bystep(transformation, input_model, input_metamodel, npartition, sc, nstep, storage)
           println(line + "," + res._1 + "," + res._2.mkString(","))
//       } catch {
//           case e: Exception => println(e.getLocalizedMessage)
//       }
    }
}
