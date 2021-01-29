package org.atlanmod.transformation

import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.util.{C2RUtil, FileUtil, TimeUtil}

object Main {

    final val NTEST = 1
    final val NCORE = 4

    final val GLOBAL_DIR_RES_NAME = "c2r_results"
    final val DIR_RES_NAME = GLOBAL_DIR_RES_NAME + "/" + TimeUtil.strLocalTime
    final val FILE_RES_DATA_EXT = "csv"
    final val FILE_RES_ANALYSE_EXT = "r"
    final val FILE_RES_NAME = "results"


    def init(): Unit ={
        FileUtil.create_if_not_exits(GLOBAL_DIR_RES_NAME)
        FileUtil.create_if_not_exits(DIR_RES_NAME)
    }

    def sizes(): List[(Int, Int)] = {
        List((10, 10))
    }


    def run_experiment (size: (Int, Int), times: Int, ncore: Int) : List[String] = {
        val methods = C2RUtil.get_methods()
        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.class2relational()
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        C2RUtil.running_test_csv(methods, transformation, metamodel, times, ncore, size._1, size._2)
    }

    def getcores(): List[Int] = {
        List(2)
    }

    def main(args: Array[String]) : Unit = {
        init()
        val header = "fullname,par or seq,ncore,technique,total size,classes,attributes,combo,global time," +
          "step1 time,step2 time,step3 time"
        var filenames : List[String] = List()
        for (ncore <- getcores()){
            for (size <- sizes()){
                val res_csv_lines = run_experiment(size, NTEST, ncore)
                println(res_csv_lines.mkString("\n"))
                val filename = size._1 + "_" + size._2 + "_" + ncore + ".csv"
                val filename_csv = DIR_RES_NAME + "/" + filename
//                CSVUtil.writeCSV(filename_csv, header, res_csv_lines)
                filenames = filename :: filenames
            }
        }
        val filename_rmd = DIR_RES_NAME + "/result" + ".rmd"
//        FileUtil.write_content(filename_rmd, C2RUtil.make_rmd_content(filenames))
    }


}
