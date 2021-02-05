package org.atlanmod.transformation

import org.atlanmod.util.{C2RUtil, FileUtil, TimeUtil}

object Main {

    final val NTEST = 30
    final val NCORE = 2

    final val GLOBAL_DIR_RES_NAME = "c2r_results"
    final val DIR_RES_NAME = GLOBAL_DIR_RES_NAME + "/" + TimeUtil.strLocalTime
    final val FILE_RES_DATA_EXT = "csv"
    final val FILE_RES_ANALYSE_EXT = "r"
    final val FILE_RES_NAME = "results"


    def init(): Unit ={
        FileUtil.create_if_not_exits(GLOBAL_DIR_RES_NAME)
        FileUtil.create_if_not_exits(DIR_RES_NAME)
    }

    def sizes(): List[(Int, Int, Int)] = {

        List((10, 2, 1), (20, 4, 2), (30, 6, 3), (50, 10, 5), (100, 20, 10), (200, 40, 20), (300, 60, 30), (500, 100, 50),
            (10, 2, 2), (20, 4, 4), (30, 6, 6), (50, 10, 10), (100, 20, 20), (200, 40, 40), (300, 60, 60), (500, 100, 100))
          .sortBy(C2RUtil.size_model)
    }

    def main(args: Array[String]): Unit = {
        print("")
    }

// TODO: rehabilitate the code for experiments

//    def run_experiment (size: (Int, Int, Int), times: Int, ncore: Int) : List[String] = {
//        val methods = C2RUtil.get_methods()
//        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.class2relationalMV()
//        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
//        C2RUtil.running_test_csv(methods, transformation, metamodel, times, ncore, size._1, size._2, size._3)
//    }
//
//    def getcores(): List[Int] = {
//        List(0, 1, 2, 4, 6, 8)
//    }
//
//    def main(args: Array[String]) : Unit = {
//        val ncore = if (args.length >= 1) args(0).toInt else NCORE
//        init()
//        val header = "fullname,par or seq,ncore,technique,total size,classes,attributes,multivalued,combo,global time," +
//          "step1 time,step2 time,step3 time"
//        var filenames : List[String] = List()
//
//        try {
//            for (size <- sizes()) {
//                val res_csv_lines = run_experiment(size, NTEST, ncore)
//                println(res_csv_lines.mkString("\n"))
//                val filename = size._1 + "_" + size._2 + "_" + size._3 + "_" + ncore + ".csv"
//                val filename_csv = DIR_RES_NAME + "/" + filename
//                CSVUtil.writeCSV(filename_csv, header, res_csv_lines)
//                filenames = filename :: filenames
//            }
//        }catch {
//            case _: Exception =>
//        }
//
//        val filename_rmd = DIR_RES_NAME + "/result" + ".rmd"
//        FileUtil.write_content(filename_rmd, C2RUtil.make_rmd_content(filenames))
//    }


}
