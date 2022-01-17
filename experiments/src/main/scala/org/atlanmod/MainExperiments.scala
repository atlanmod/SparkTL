package org.atlanmod

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.classmodel.metamodel.{ClassMetamodel, ClassMetamodelNaive, ClassMetamodelWithMap}
import org.atlanmod.class2relational.model.relationalmodel.metamodel.{RelationalMetamodel, RelationalMetamodelNaive, RelationalMetamodelWithMap}
import org.atlanmod.class2relational.transformation.{Class2Relational, Relational2Class}
import org.atlanmod.dblpinfo.model.dblp.metamodel.{DblpMetamodel, DblpMetamodelNaive, DblpMetamodelWithMap}
import org.atlanmod.dblpinfo.tranformation.{ICMTActiveAuthors, ICMTAuthors, InactiveICMTButActiveAuthors, JournalISTActiveAuthors}
import org.atlanmod.findcouples.ModelSamples
import org.atlanmod.findcouples.model.movie.MovieJSONLoader
import org.atlanmod.findcouples.model.movie.metamodel.{MovieMetamodel, MovieMetamodelNaive, MovieMetamodelWithMap}
import org.atlanmod.findcouples.transformation.dynamic.{FindCouples, Identity}
import org.atlanmod.parallel._
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}

import scala.annotation.tailrec

object MainExperiments {

    var sc: SparkContext = _
    val csv_header: String =
        "solution,links_type,case,element,link," +
          "executor,core,partition,storageLevel," +
          "sleeping_guard,sleeping_instantiate,sleeping_apply," +
          "total_time,time_tuples,time_instantiate,time_extract,time_broadcast,time_apply," +
          "output_element,output_link"

    // <editor-fold desc="Parameters">

    // Configured values from SparkConf
    var executor_cores = 1
    var num_executors = 1
    var executor_memory = "1g"
    var partition = 1

    // Transformation
    final val DEFAULT_TRANSFORMATION: String = "IMDBFindCouples"
    var tr_case: String = DEFAULT_TRANSFORMATION

    val DEFAULT_HEADER: Boolean = false
    var header: Boolean = DEFAULT_HEADER

    // Possible sleeping times
    final val DEFAULT_SLEEPING_GUARD: Int = 0
    var sleeping_guard: Int = DEFAULT_SLEEPING_GUARD
    final val DEFAULT_SLEEPING_INSTANTIATE: Int = 0
    var sleeping_instantiate: Int = DEFAULT_SLEEPING_INSTANTIATE
    final val DEFAULT_SLEEPING_APPLY: Int = 0
    var sleeping_apply: Int = DEFAULT_SLEEPING_APPLY

    // For input model
    final val DEFAULT_INPUT_TYPE: String = "size"
    var input_type: String = DEFAULT_INPUT_TYPE
    final val DEFAULT_SIZE: Int = 100
    var size: Int = DEFAULT_SIZE
    var files: List[String] = List()

    // To process partial computation
    //    final val DEFAULT_NSTEP: Int = 5
    //    var nstep: Int = DEFAULT_NSTEP
    final val DEFAULT_TLS_SOLUTION: String = "map"
    var tls_solution: String = DEFAULT_TLS_SOLUTION
    final val DEFAULT_LINKS_SOLUTION: String = "map"
    var links_solution: String = DEFAULT_LINKS_SOLUTION

    // Storage Level of RDDs
    final val DEFAULT_STORAGE: StorageLevel = StorageLevel.MEMORY_AND_DISK
    var storage: StorageLevel = DEFAULT_STORAGE
    final val DEFAULT_STORAGE_STRING: String = "MEMORY_AND_DISK"
    var storage_string: String = DEFAULT_STORAGE_STRING

    // </editor-fold>
    // <editor-fold desc="Initialization methods">

    @tailrec
    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-solution" :: sol :: args =>
                tls_solution = sol
                parseArgs(args)
            case "-links" :: links :: args =>
                links_solution = links
                parseArgs(args)
            case "-file" :: file :: args =>
                files = file :: files
                input_type = "files"
                parseArgs(args)
            case "-size" :: s :: args =>
                size = s.toInt
                input_type = "size"
                parseArgs(args)
            case "-persist" :: level :: args =>
                storage = StorageLevel.fromString(level)
                storage_string = level
                parseArgs(args)
            case "-executor" :: exe :: args =>
                num_executors = exe.toInt
                parseArgs(args)
            case "-header" :: args =>
                header = true
                parseArgs(args)
            case "-case" :: c :: args =>
                tr_case = c
                parseArgs(args)
            case _ :: args => parseArgs(args)
            case List() =>
        }
    }

    def initSparkContext(): Unit = {
        val conf = new SparkConf()
        conf.setIfMissing("spark.master", "local[" + num_executors + "]")
        conf.setIfMissing("spark.app.name", "SparkTE")
        conf.setIfMissing("spark.executor.memory", "1g")
        conf.setIfMissing("spark.executor.cores", "1")
        sc = new SparkContext(conf)
    }

    def init(args: Array[String]): Unit = {
        parseArgs(args.toList)
        initSparkContext()
        executor_cores = sc.getConf.get("spark.executor.cores").toInt
        partition = executor_cores * num_executors * 1
    }

    // </editor-fold>
    // <editor-fold desc="Utils functions to setup the transformation">

    def getTransformation(name: String, links_type: String, itr_memoization: Boolean = true): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        name match {
            case "Class2Relational" =>
                val class_metamodel: ClassMetamodel =
                    links_type match {
                        case "default" => ClassMetamodelNaive
                        case "map" => ClassMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                val rel_metamodel : RelationalMetamodel =
                    links_type match {
                        case "default" => RelationalMetamodelNaive
                        case "map" => RelationalMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                Class2Relational.class2relational(class_metamodel, rel_metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "Relational2Class" =>
                val class_metamodel: ClassMetamodel =
                    links_type match {
                        case "default" => ClassMetamodelNaive
                        case "map" => ClassMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                val rel_metamodel : RelationalMetamodel =
                    links_type match {
                        case "default" => RelationalMetamodelNaive
                        case "map" => RelationalMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                Relational2Class.relational2class(rel_metamodel, class_metamodel, sleeping_guard = sleeping_guard, sleeping_instantiate = sleeping_instantiate, sleeping_apply = sleeping_apply)
            case "IMDBFindCouples" =>
                val metamodel : MovieMetamodel =
                    links_type match {
                        case "default" => MovieMetamodelNaive
                        case "map" => MovieMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                FindCouples.findcouples_imdb(metamodel, itr_memoization, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "IMDBIdentity" =>
                val metamodel : MovieMetamodel =
                    links_type match {
                        case "default" => MovieMetamodelNaive
                        case "map" => MovieMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                Identity.identity_imdb(metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v1" =>
                val metamodel : DblpMetamodel =
                    links_type match {
                        case "default" => DblpMetamodelNaive
                        case "map" => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                ICMTAuthors.find(metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v2" =>
                val metamodel : DblpMetamodel =
                    links_type match {
                        case "default" => DblpMetamodelNaive
                        case "map" => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                ICMTActiveAuthors.find(metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v3" =>
                val metamodel : DblpMetamodel =
                    links_type match {
                        case "default" => DblpMetamodelNaive
                        case "map" => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                InactiveICMTButActiveAuthors.find(metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v4" =>
                val metamodel : DblpMetamodel =
                    links_type match {
                        case "default" => DblpMetamodelNaive
                        case "map" => DblpMetamodelWithMap
                        case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)
                    }
                JournalISTActiveAuthors.find(metamodel, sleeping_guard, sleeping_instantiate, sleeping_apply)
            case _ => throw new Exception("Impossible to get the metamodel with the following arguments: " + name + "; " + links_type)

        }
    }

    def getMetamodel(name: String): DynamicMetamodel[DynamicElement, DynamicLink] =
        name match {
            case "Class2Relational" => ClassMetamodelNaive.metamodel
            case "Class2RelationalSimple" => ClassMetamodelNaive.metamodel
            case "Relational2Class" => RelationalMetamodelNaive.metamodel
            case "Relational2ClassStrong" => RelationalMetamodelNaive.metamodel
            case "IMDBFindCouples" => MovieMetamodelNaive.metamodel
            case "IMDBIdentity" => MovieMetamodelNaive.metamodel
            case "DBLP2INFO_v1" => DblpMetamodelNaive.metamodel
            case "DBLP2INFO_v2" => DblpMetamodelNaive.metamodel
            case "DBLP2INFO_v3" => DblpMetamodelNaive.metamodel
            case "DBLP2INFO_v4" => DblpMetamodelNaive.metamodel
            case _ => throw new Exception("Impossible to get the metamodel. Unknown transformation: " + name)
        }

    def getModel(mm: DynamicMetamodel[DynamicElement, DynamicLink], input: String, files: List[String]): DynamicModel = {
        if (mm == ClassMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.class2relational.model.ModelSamples.getReplicatedClassSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Class model from files is not supported yet")
            }
        else if (mm == RelationalMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.class2relational.model.ModelSamples.getReplicatedRelationalSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Relational model from files is not supported yet")
            }
        else if (mm == MovieMetamodelNaive.metamodel)
            input match {
                case "size" => ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" =>
                    (files.find(f => f.contains("movie")), files.find(f => f.contains("actor")), files.find(f => f.contains("link"))) match {
                        case (Some(movie_file), Some(actor_file), Some(link_file)) =>
                            MovieJSONLoader.load(actor_file, movie_file, link_file)
                        case (None, _, _) => throw new Exception("JSON file containing movies not declared")
                        case (_, None, _) => throw new Exception("JSON file containing actors not declared")
                        case (_, _, None) => throw new Exception("TXT file containing links not declared")
                    }
            }
        else if (mm == DblpMetamodelNaive.metamodel)
            input match {
                case "size" => org.atlanmod.dblpinfo.model.ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Dblp model from files is not supported yet")
            }
        else throw new Exception("Impossible to generate a model. Unknown metamodel: " + mm.getClass.getName)
    }

    // </editor-fold>

    def main(args: Array[String]): Unit = {
        init(args)

        val transformation = getTransformation(tr_case, links_solution)
        val input_metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = getMetamodel(tr_case)
        val input_model: DynamicModel = getModel(input_metamodel, input_type, files)

        val res: (TimeResult, ModelResult[DynamicElement, DynamicLink]) = {
            tls_solution match {
                case "default" => TransformationEngineTwoPhaseByRule.execute(transformation, input_model, input_metamodel, partition, sc)
                case "variant" => TransformationEngineTwoPhaseByRuleVariant.execute(transformation, input_model, input_metamodel, partition, sc)
                case "fold" => TransformationEngineTwoPhaseByRuleWithFold.execute(transformation, input_model, input_metamodel, partition, sc)
                case "map" => TransformationEngineTwoPhaseByRuleWithMap.execute(transformation, input_model, input_metamodel, partition, sc)
                case "cartesian" => TransformationEngineTwoPhaseByRuleCartesianWithMap.execute(transformation, input_model, input_metamodel, partition, sc)
                case _ => throw new Exception("The parallel solution must be specified for this specific main Scala class.")
            }
        }
        val execution_result = new ExecutionResult(tls_solution, links_solution, tr_case, input_model, num_executors, executor_cores,
            partition, storage_string, res._1, res._2)

        if (header) println(execution_result.csv_header)
        println(execution_result.csv)
    }

}
//limit : 20 000 <- before
//map,map,IMDBFindCouples,24999,73074,4,1,16,MEMORY_AND_DISK,0,0,0,1208866.399652,151.854239,200824.700824,52.170621,0.0,1007837.673968,32691,40383 <- it expr
//map,map,IMDBFindCouples,24999,73074,4,1,16,MEMORY_AND_DISK,0,0,0,262094.14652100002,177.546724,111736.984679,36.885424,0.0,150142.729694,32691,40383 <- memoization
//map,map,IMDBFindCouples,99996,292296,8,1,8,MEMORY_AND_DISK,0,0,0,49948.130204,548.386828,19334.971587,114.342279,0.0,29950.42951,130764,161532 <- fixed wrong metamodel
//