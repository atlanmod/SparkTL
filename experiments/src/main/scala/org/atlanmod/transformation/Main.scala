package org.atlanmod.transformation

import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.util.{C2RUtil, CSVUtil, FileUtil, TimeUtil}

object Main {

    final val NTEST = 10
    final val NCORE = 0

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
//                List((5,5), (10,5), (10, 20), (20, 20), (50, 50))
        List((20, 4), (50, 10), (100, 20), (150, 30), (200, 40), (250, 50), (300, 60), (400, 80), (500, 100))
    }


    def run_experiment (size: (Int, Int), times: Int, ncore: Int) : List[String] = {
        val methods = C2RUtil.get_methods()
        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.transformation()
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        C2RUtil.running_test_csv(methods, transformation, metamodel, times, ncore, size._1, size._2)
    }

    def getcores(): List[Int] = {
        List(0)
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
                CSVUtil.writeCSV(filename_csv, header, res_csv_lines)
                filenames = filename :: filenames
            }
        }
        val filename_rmd = DIR_RES_NAME + "/result" + ".rmd"
        FileUtil.write_content(filename_rmd, C2RUtil.make_rmd_content(filenames))
    }


}
