package org.atlanmod.transformation

import org.atlanmod.class2relational.model.classmodel.ClassMetamodel
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.util._

import scala.annotation.tailrec
import scala.collection.mutable

object Main_Class2Relational_Dumb {

    final val DEFAULT_NTEST = 1
    final val DEFAULT_SEQ = true
    final val DEFAULT_NCORE = 1
    final val DEFAULT_MODE = "both"
    final val DEFAULT_PRINT_FILE = false
    final val DEFAULT_PRINT_RFILE = false
    final val DEFAULT_PRINT_SCREEN = true
    final val DEFAULT_SIZES = List(10)

//    final val GLOBAL_DIR_RES_NAME = "c2r_results"
//    final val DIR_RES_NAME = GLOBAL_DIR_RES_NAME + "/" + TimeUtil.strLocalTime
    final val DEFAULT_DIR_RES_NAME = "~/result_c2r/"
    final val FILE_RES_DATA_EXT = "csv"
    final val FILE_RES_ANALYSE_EXT = "r"
    final val FILE_RES_NAME = "results"
    final val DEFAULT_METHOD = "all"

    var path_file: String = DEFAULT_DIR_RES_NAME
    var ncore: Int = DEFAULT_NCORE
    var sequential: Boolean = DEFAULT_SEQ
    var times: Int = DEFAULT_NTEST
    var sizes: List[Int] = DEFAULT_SIZES
    var print_file: Boolean = DEFAULT_PRINT_FILE
    var print_rfile: Boolean = DEFAULT_PRINT_RFILE
    var print_screen: Boolean = DEFAULT_PRINT_SCREEN
    var method: String = DEFAULT_METHOD
    var mode: String = DEFAULT_MODE



    def init(): Unit = {
//        if (print_file) FileUtil.create_if_not_exits(GLOBAL_DIR_RES_NAME)
        if (print_file) FileUtil.create_if_not_exits(path_file)
    }

    def run_test_csv_lines(methods: List[(String, String, TransformationUtil.transformation_function)],
                     transformation: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink],
                     metamodel: DynamicMetamodel[DynamicElement, DynamicLink],
                     times: Int, ncore: Int, patterns: Int): List[String] = {
        // create model
        val startTimeMillis = System.currentTimeMillis()
        val model = C2RUtil.get_model_from_n_patterns(patterns)
        val endTimeMillis = System.currentTimeMillis()
        val mili = endTimeMillis - startTimeMillis
        println("MODEL GENERATED IN "+mili+"ms")
        if(print_screen) println("size: "+ model.allModelElements.size+ " elements, " + model.allModelLinks.size + "links"+ "; ")
        // execution + get the results (computation time)
        val results: mutable.HashMap[(String, String), List[(Double, List[Double])]]=
            TransformationUtil.apply(methods, transformation, model, metamodel, times, ncore, print_screen)
        var lines: List[String] = List()
        // for each method: treat of the lines
        for((parseq, method) <- results.keys) {
            val fullname = parseq + "." + method
            val total_size = (9 * patterns).toString
            val number_classes = (2 * patterns).toString
            val number_attributes =  (5 * patterns).toString
            val number_multivalued = (2 * patterns).toString
            val combo = total_size + "_" + number_classes + "_" + number_attributes + "_" + number_multivalued
            // for each execution, create a csv line
            results.get((parseq, method)) match {
                case Some(res: List[(Double, List[Double])]) =>
                    for(result <- res){
                        val global_time = result._1
                        val step1_time = if (result._2.length < 1) "0" else result._2.head.toString
                        val step2_time = if (result._2.length < 2) "0" else result._2(1).toString
                        val step3_time = if (result._2.length < 3) "0" else result._2(2).toString
                        lines =
                          List(fullname, parseq, ncore, method,
                              total_size, number_classes, number_attributes, number_multivalued, combo,
                              global_time, step1_time, step2_time,step3_time).mkString(",") :: lines
                    }
                case _ =>
            }
        }
        val header = "fullname,parseq,nexecutor,method,total_size,number_classes,number_attributes, " +
          "number_multivalued,combo,global_time,step1_time,step2_time,step3_time"
        header :: lines
    }

    def run_experiment_sizes_csv_files(sizes: List[Int], times: Int, ncore: Int): List[String] = {
        val methods = TransformationUtil.get_methods(method)
        val transformation = org.atlanmod.class2relational.transformation.dynamic.Class2Relational.class2relational()
        val metamodel = ClassMetamodel.metamodel
        var filenames : List[String] = List()
        try {
            for (size <- sizes){
                val lines = run_test_csv_lines(methods, transformation, metamodel, times, ncore, size)
                val filename =  "c2r_" + size.toString + "_" + ncore.toString + ".csv"
                if (print_file) {
                    CSVUtil.writeCSV(path_file + "/" + filename, lines)
                }
                filenames = filename :: filenames
            }
        }catch{
            case _: Exception =>
        }
        filenames
    }

    @tailrec
    def parseArgs(args: List[String]): Unit = {
        args match {
            case "--nexecutor" :: core :: args_ =>
                sequential = core.toInt == 0
                ncore = core.toInt
                parseArgs(args_)
            case "--s" :: size :: args_ =>
                sizes = List(size.toInt)
                parseArgs(args_)
            case "--size" :: size :: args_ =>
                sizes = List(size.toInt)
                parseArgs(args_)
            case "--n" :: n :: args_ => {
                times = n.toInt
                parseArgs(args_)
            }
            case "--ntests" :: n :: args_ =>  {
                times = n.toInt
                parseArgs(args_)
            }
            case "-csv" :: args_ =>{
                print_file = true
                parseArgs(args_)
            }
            case "-rfile" :: args_ =>{
                print_file = true
                print_rfile = true
                parseArgs(args_)
            }
            case "--m" :: m :: args_ =>{
                method = m
                parseArgs(args_)
            }
            case "--method" :: m :: args_ =>{
                method = m
                parseArgs(args_)
            }
            case "-print" :: args_ =>{
                print_screen = true
                parseArgs(args_)
            }
            case "--path" :: path :: args_ =>{
                path_file = path
                parseArgs(args_)
            }
            case _ :: args_ => parseArgs(args_)
            case _ =>
        }
    }

    def main(args: Array[String]): Unit = {
        parseArgs(args.toList)
        init()
        val filenames = run_experiment_sizes_csv_files(sizes, times, ncore)
        val filename_rmd = DEFAULT_DIR_RES_NAME + "/result" + ".rmd"
        if(print_rfile) FileUtil.write_content(filename_rmd, TransformationUtil.make_rmd_content(filenames))
    }

}
