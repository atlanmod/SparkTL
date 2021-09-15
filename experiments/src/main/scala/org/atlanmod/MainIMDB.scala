package org.atlanmod

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

    var json_actors: String = "/home/jolan/actors_imdb-0.1.json"
    var json_movies: String = "/home/jolan/movies_imdb-0.1.json"
    var txt_links: String = "/home/jolan/links_imdb-0.1.txt"

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
            case _ :: args => parseArgs(args)
            case List() => if (npartition == DEFAULT_NPARTITION) npartition = ncore * nexecutor
        }
    }

    def getContext(): SparkContext = {
        val conf = new SparkConf()
        conf.setMaster("local[2]")
        conf.setAppName("Test")
        new SparkContext(conf)
    }

    def main(args: Array[String]): Unit = {
//       try {
           parseArgs(args.toList)
           val sc = getContext()
           val transformation = org.atlanmod.findcouples.transformation.dynamic.FindCouples.findcouples_imdb
           val input_model : MovieModel = MovieJSONLoader.load(json_actors,json_movies,txt_links)
           val input_metamodel = MovieMetamodel.metamodel
           var line = List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
           val res = TransformationEngineTwoPhaseByRule.execute_bystep(transformation, input_model, input_metamodel, npartition, sc, nstep)
           line = List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition).mkString(",")
           println(line + "," + res._1 + "," + res._2.mkString(","))
//       } catch {
//           case e: Exception => println(e.getLocalizedMessage)
//       }
    }
}
