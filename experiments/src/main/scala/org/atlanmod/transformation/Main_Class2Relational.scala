package org.atlanmod.transformation

import org.atlanmod.model.classmodel.ClassMetamodel
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.util._

import scala.collection.mutable

object Main_Class2Relational {

    final val NTEST = 30
    final val NCORE = 2
    final val SIZES = List(1, 10, 20, 50, 100, 500, 1000, 2000, 3500, 5000, 7500, 10000, 25000, 50000, 75000, 100000)

    final val GLOBAL_DIR_RES_NAME = "c2r_results"
    final val DIR_RES_NAME = GLOBAL_DIR_RES_NAME + "/" + TimeUtil.strLocalTime
    final val FILE_RES_DATA_EXT = "csv"
    final val FILE_RES_ANALYSE_EXT = "r"
    final val FILE_RES_NAME = "results"

    def init(): Unit = {
        FileUtil.create_if_not_exits(GLOBAL_DIR_RES_NAME)
        FileUtil.create_if_not_exits(DIR_RES_NAME)
    }

    def run_test_csv_lines(methods: List[(String, String, TransformationUtil.transformation_function)],
                     transformation: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink],
                     metamodel: DynamicMetamodel[DynamicElement, DynamicLink],
                     times: Int, ncore: Int, patterns: Int): List[String] = {
        // create model
        val model = R2CUtil.get_model_from_n_patterns(patterns)
        print("size: "+ model.allModelElements.size+ " elements, " + model.allModelLinks.size + "links"+ "; ")
        // execution + get the results (computation time)
        val results: mutable.HashMap[(String, String), List[(Double, List[Double])]]=
            TransformationUtil.apply(methods, transformation, model, metamodel, times, ncore)
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
        val header = "fullname,parseq,ncore,method,total_size,number_classes,number_attributes, " +
          "number_multivalued,combo,global_time,step1_time,step2_time,step3_time"
        // return lines + head
        header :: lines
    }

    def run_experiment_sizes_csv_files(sizes: List[Int], times: Int, ncore: Int): List[String] = {
        println("NCORE = " + ncore)
        val methods = TransformationUtil.get_methods()
        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.class2relational()
        val metamodel = ClassMetamodel.metamodel
        var filenames : List[String] = List()
        try {
            for (size <- sizes){
                val lines = run_test_csv_lines(methods, transformation, metamodel, times, ncore, size)
                val filename =  size.toString + "_" + ncore.toString + ".csv"
                CSVUtil.writeCSV(DIR_RES_NAME + "/" + filename, lines)
                filenames = filename :: filenames
            }
        }catch{
            case _: Exception =>
        }
        filenames
    }

    def main(args: Array[String]): Unit = {
        init()
        val ncore = if (args.length >= 1) args(0).toInt else NCORE
        val times = NTEST
        val sizes = SIZES
        val filenames = run_experiment_sizes_csv_files(sizes, times, ncore)
        val filename_rmd = DIR_RES_NAME + "/result" + ".rmd"
        FileUtil.write_content(filename_rmd, TransformationUtil.make_rmd_content(filenames))
    }

}
