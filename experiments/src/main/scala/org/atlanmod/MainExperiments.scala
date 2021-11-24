package org.atlanmod

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.classmodel.ClassMetamodel
import org.atlanmod.class2relational.model.relationalmodel.RelationalMetamodel
import org.atlanmod.class2relational.transformation.{Class2Relational, Class2RelationalSimple, Relational2Class, Relational2ClassStrong}
import org.atlanmod.dblpinfo.model.dblp.DblpMetamodel
import org.atlanmod.dblpinfo.tranformation.{ICMTActiveAuthors, ICMTAuthors, InactiveICMTButActiveAuthors, JournalISTActiveAuthors}
import org.atlanmod.findcouples.ModelSamples
import org.atlanmod.findcouples.model.movie.{MovieJSONLoader, MovieMetamodel}
import org.atlanmod.findcouples.transformation.dynamic.{FindCouples, Identity}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.transformation.parallel._

import scala.annotation.tailrec

object MainExperiments {

    var sc: SparkContext = _
    val csv_header: String =
        "solution,case,element,link," +
          "executor,core,partition,storageLevel," +
          "sleeping_guard,sleeping_instantiate,sleeping_apply," +
          "total_time,time_tuples,time_instantiate,time_extract,time_broadcast,time_apply," +
          "output_element,output_link"

    // <editor-fold desc="Parameters">

    // Configured values from SparkConf
    var executor_cores = 1
    var num_executors = 1
    var executor_memory = "1g"
    var partition = 4

    // Transformation
    final val DEFAULT_TRANSFORMATION: String = "Class2Relational"
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
    final val DEFAULT_SOLUTION: String = "default"
    var solution: String = DEFAULT_SOLUTION

    // Storage Level of RDDs
    final val DEFAULT_STORAGE: StorageLevel = StorageLevel.MEMORY_AND_DISK
    var storage:  StorageLevel = DEFAULT_STORAGE
    final val DEFAULT_STORAGE_STRING: String = "MEMORY_AND_DISK"
    var storage_string:  String = DEFAULT_STORAGE_STRING

    // </editor-fold>
    // <editor-fold desc="Initialization methods">

    @tailrec
    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-solution" :: sol :: args =>
                solution = sol
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
                executor_cores = sc.getConf.get("spark.executor.cores").toInt
                partition = executor_cores * num_executors * 4
        }
    }

    def initSparkContext(): Unit = {
        val conf = new SparkConf()
        conf.setIfMissing("spark.master","local")
        conf.setIfMissing("spark.app.name","SparkTE")
        conf.setIfMissing("spark.executor.memory","1g")
        conf.setIfMissing("spark.executor.cores","1")
        sc = new SparkContext(conf)
    }

    def init(args: Array[String]): Unit = {
        initSparkContext()
        parseArgs(args.toList)
    }
    // </editor-fold>
    // <editor-fold desc="Utils functions to setup the transformation">

    def getTransformation(name: String): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        name match {
            case "Class2Relational" => Class2Relational.class2relational(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "Relational2Class" => Relational2Class.relational2class(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "Class2RelationalSimple" => Class2RelationalSimple.class2relational(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "Relational2ClassStrong" => Relational2ClassStrong.relational2class(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "IMDBFindCouples" => FindCouples.findcouples_imdb(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "IMDBIdentity" => Identity.identity_imdb(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v1" => ICMTAuthors.find(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v2" => ICMTActiveAuthors.find(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v3" => InactiveICMTButActiveAuthors.find(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case "DBLP2INFO_v4" => JournalISTActiveAuthors.find(sleeping_guard, sleeping_instantiate, sleeping_apply)
            case _ => throw new Exception("Unknown transformation: " + name )
        }

    def getMetamodel(name: String): DynamicMetamodel[DynamicElement, DynamicLink] =
        name match {
            case "Class2Relational" => ClassMetamodel.metamodel
            case "Class2RelationalSimple" => ClassMetamodel.metamodel
            case "Relational2Class" => RelationalMetamodel.metamodel
            case "Relational2ClassStrong" => RelationalMetamodel.metamodel
            case "IMDBFindCouples" => MovieMetamodel.metamodel
            case "IMDBIdentity" => MovieMetamodel.metamodel
            case "DBLP2INFO_v1" => DblpMetamodel.metamodel
            case "DBLP2INFO_v2" => DblpMetamodel.metamodel
            case "DBLP2INFO_v3" => DblpMetamodel.metamodel
            case "DBLP2INFO_v4" => DblpMetamodel.metamodel
            case _ => throw new Exception("Impossible to get the metamodel. Unknown transformation: " + name)
        }

    def getModel(mm: DynamicMetamodel[DynamicElement, DynamicLink], input: String, files: List[String]): DynamicModel = {
        if (mm == ClassMetamodel.metamodel)
            input match {
                case "size" =>  org.atlanmod.class2relational.model.ModelSamples.getReplicatedClassSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Class model from files is not supported yet")
            }
        else if (mm == RelationalMetamodel.metamodel)
            input match {
                case "size" => org.atlanmod.class2relational.model.ModelSamples.getReplicatedRelationalSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Relational model from files is not supported yet")
            }
        else if (mm == MovieMetamodel.metamodel)
            input match {
                case "size" => ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" =>
                    println(files.mkString(";"))
                    (files.find(f => f.contains("movie")), files.find(f => f.contains("actor")), files.find(f => f.contains("link"))) match {
                        case (Some(movie_file), Some(actor_file), Some(link_file)) =>
                            MovieJSONLoader.load(actor_file, movie_file, link_file)
                        case (None, _, _) =>throw new Exception("JSON file containing movies not declared")
                        case (_, None, _) =>throw new Exception("JSON file containing actors not declared")
                        case (_, _, None) =>throw new Exception("TXT file containing links not declared")
                    }
            }
        else if (mm == DblpMetamodel.metamodel)
            input match {
                case "size" => org.atlanmod.dblpinfo.model.ModelSamples.getReplicatedSimple(size).asInstanceOf[DynamicModel]
                case "files" => throw new Exception("Generating a Dblp model from files is not supported yet")
            }
        else throw new Exception("Impossible to generate a model. Unknown metamodel: " + mm.getClass.getName)
    }
    // </editor-fold>

    def main(args: Array[String]): Unit = {
        init(args)
        val transformation = getTransformation(tr_case)
        val input_metamodel: DynamicMetamodel[DynamicElement, DynamicLink]  = getMetamodel(tr_case)
        val input_model: DynamicModel = getModel(input_metamodel, input_type, files)

        var res : (Double, List[Double], (Int, Int)) = null

        solution match {
            case "default" => res = TransformationEngineTwoPhaseByRule.execute(transformation, input_model, input_metamodel, partition, sc)
            case "variant" => res = TransformationEngineTwoPhaseByRuleVariant.execute(transformation, input_model, input_metamodel, partition, sc)
            case "fold" => res = TransformationEngineTwoPhaseByRuleWithFold.execute(transformation, input_model, input_metamodel, partition, sc)
            case "map" => res = TransformationEngineTwoPhaseByRuleWithMap.execute(transformation, input_model, input_metamodel, partition, sc)
            case "single" => res = TransformationEngineSinglePhaseByRule.execute(transformation, input_model, input_metamodel, partition, sc)
            case _ => throw new Exception("The parallel solution must be specified for this specific main Scala class.")
        }

        val line = List(
            solution,tr_case, input_model.numberOfElements, input_model.numberOfLinks,
            num_executors, executor_cores, partition, storage_string,
            sleeping_guard, sleeping_instantiate, sleeping_apply,
            res._1).mkString(",") + "," + res._2.mkString(",") + "," + res._3._1 + "," + res._3._2

        if (header) println(csv_header)
        println(line)

    }

}
