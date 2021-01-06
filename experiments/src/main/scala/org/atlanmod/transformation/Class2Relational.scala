package org.atlanmod.transformation

import org.apache.spark.SparkContext
import org.atlanmod.model.dynamic.classModel._
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.{Model, Transformation}
import org.atlanmod.tl.util.SparkUtil
import org.atlanmod.util.{FileUtil, TimeUtil}

object Class2Relational{

    final val NTESTS = 30
    final val NCORE = 1

    type SME = DynamicElement
    type SML = DynamicLink
    type SMC = String
    type SMR = String
    type TME = DynamicElement
    type TML = DynamicLink

    type transformation_type = Transformation[SME, SML, SMC, TME, TML]
    type source_model = ClassModel
    type source_metamodel = DynamicMetamodel[DynamicElement, DynamicLink]
    type target_model = Model[DynamicElement, DynamicLink]

    type transformation_function = (transformation_type, source_model, source_metamodel, SparkContext) => (Double, List[Double])

    final val GLOBAL_DIR_RES_NAME = "c2r_results"
    final val DIR_RES_NAME = GLOBAL_DIR_RES_NAME + "/" + TimeUtil.strLocalTime
    final val FILE_RES_DATA_EXT = "csv"
    final val FILE_RES_ANALYSE_EXT = "r"
    final val FILE_RES_NAME = "results"

    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1): ClassModel = {
        var elements : List[ClassElement] = List()
        var links : List[ClassLink] = List()
        for(i <- 1 to nclass){
            val cc = new ClassClass(i.toString, "")
            elements = cc :: elements
            var cc_attributes : List[ClassAttribute] = List()
            for (j <- 1 to nattribute) {
                val ca = new ClassAttribute(i.toString + "." + j.toString, "")
                elements = ca :: elements
                links = new AttributeToClass(ca, cc) :: links
                cc_attributes = ca :: cc_attributes
            }
            links = new ClassToAttributes(cc, cc_attributes) :: links

        }
        new ClassModel(elements, links)
    }

    def run_test(tr_foo: transformation_function, tr: transformation_type,
                 sm: source_model, mm: source_metamodel, sc: SparkContext): (Double, List[Double]) = {
        tr_foo(tr, sm, mm, sc)
    }

    def run_tests(tr_foo: transformation_function, tr: transformation_type,
                 sm: source_model, mm: source_metamodel, sc: SparkContext, times: Int): List[(Double, List[Double])] = {
        var res: List[(Double, List[Double])] = List()
        for(_ <- 1 to times) {
            res = run_test(tr_foo, tr, sm, mm, sc) :: res
        }
        res.reverse
    }

    def get_tests(): List[(String, String, transformation_function)] = {
        val res : List[(String, String, transformation_function)] =
            List(
                ("seq", "simple", (tr, m, mm, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("par", "simple", (tr, m, mm, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineImpl.execute(tr, m, mm, sc)),
                ("seq", "byrule", (tr, m, mm, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("par", "byrule", (tr, m, mm, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineByRule.execute(tr, m, mm, sc)),
                ("seq", "twophase", (tr, m, mm, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
                ("par", "twophase", (tr, m, mm, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
            )
        res
    }

    def print_sizes_raw() : Unit = print(get_sizes_raw())

    private def size_model(size: (Int, Int)): Int = size._1 + size._1 * size._2

    def get_sizes_raw() : List[(Int, Int)] = {
        var results : List[(Int, Int)]= List()
        val sizes_1 = List(0, 1, 5, 10, 20, 50, 100)
        val sizes_2 = List(200, 500, 1000)
        for(i <- sizes_1){
            if (i != 0)
                for(j <- sizes_1) results = (i, j) :: results
        }

        for(i <- sizes_1) {
                for(j <- sizes_2)
                    if (i != 0) results = (i, j) :: (j, i) :: results
                    else results = (j, i) :: results
        }

        for(i <- sizes_2) {
            for(j <- sizes_2) results = (i, j) :: results
        }
        results.sortBy(size_model)
    }

    def get_sizes() : List[(Int, Int)] = {
        get_sizes_raw()
    }

    def get_cores()  : List[Int] = List(0, 1, 2, 3, 4, 5, 6, 7, 8)

    def conduct_time_experiments() : List[String] = {
        FileUtil.create_if_not_exits(GLOBAL_DIR_RES_NAME)
        FileUtil.create_if_not_exits(DIR_RES_NAME)
        var filenames : List[String] = List()

        // -------------------------------------------------------------------------------------------------
        // Gobal setup for experiments
        // -------------------------------------------------------------------------------------------------

        val sizes = get_sizes()
        val ntests = NTESTS
        val methods = get_tests()
        val ncores = get_cores()

        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = org.atlanmod.transformation.dynamic.Class2Relational.transformation()

        for(ncore <- ncores) {
            // -------------------------------------------------------------------------------------------------
            // Gobal setup for transformation
            // -------------------------------------------------------------------------------------------------
            val sc = SparkUtil.context(ncore)

            // -------------------------------------------------------------------------------------------------
            // Header for data file (CSV)
            // -------------------------------------------------------------------------------------------------
            val header = "fullname,par or seq,ncore,technique,total size,classes,attributes,combo,global time," +
              "step1 time,step2 time,step3 time\n"

            // -------------------------------------------------------------------------------------------------
            // Create data file (CSV): one per size
            // -------------------------------------------------------------------------------------------------
            try {
                for (size <- sizes) {
                    val total_size = size_model(size)
                    var local_content = header
                    val model = dynamic_simple_model(size._1, size._2)
                    for (method <- methods) {
                        val par_seq = method._1
                        val name_test = method._2
                        val foo_test = method._3
                        if (ncore == 0 || !par_seq.equals("seq")) {
                            try {
                                val list_of_times: List[(Double, List[Double])] = run_tests(foo_test, transformation, model, metamodel, sc, ntests)
                                println("[DONE] (" + size._1 + "." + size._2 + ") with " + par_seq + "." + name_test + " on " + ncore + " cores")
                                for (result <- list_of_times) {
                                    val a_line = List(par_seq + "." + name_test, par_seq, ncore, name_test, total_size, size._1, size._2, "\"" + size._1 + "_" + size._2 + "\"", result._1,
                                        if (result._2.size < 1) "0" else result._2.head,
                                        if (result._2.size < 2) "0" else result._2(1),
                                        if (result._2.size < 3) "0" else result._2(2))
                                      .mkString(",")
                                    local_content += a_line + "\n"
                                }
                            } catch {
                                case _: Exception => throw new RuntimeException("Stopped for " + par_seq + "." +
                                  name_test + "(" + size._1 + "." + size._2 + ") with "+ ncore + " cores\n")
                            }
                        }
                    }
                    val filename_local = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + TimeUtil.strLocalTime + "_" + size + "_" + FILE_RES_DATA_EXT
                    filenames = filename_local :: filenames
                    FileUtil.write_content(filename_local, local_content)
                }
            } catch {
                case e: RuntimeException => println(e.getMessage)
            }
            sc.stop()
        }
        filenames
    }

    def create_analysis(filenames: List[String]) : Unit = {

        val header_types = "c(\"character\", \"character\", \"numeric\", \"character\", " +
          "\"numeric\",\"numeric\",\"numeric\",\"character\",\"numeric\",\"numeric\",\"numeric\",\"numeric\")"

        val methods = get_tests()

        // -------------------------------------------------------------------------------------------------
        // Create analysing file (R)
        // -------------------------------------------------------------------------------------------------

        var analyse_file_content = "library(ggplot2)\nlibrary(dplyr)\nlibrary(Rmpfr)\n" // ggplot2 is needed for printing nice plots

        // Merge resulting files into a dataframe
        var nresult = 1
        for(filename <- filenames) {
            analyse_file_content += "result" + nresult +" <- read.csv(file =" + filename +", colClasses = "+header_types+")\n"
            nresult += 1
        }
        analyse_file_content += "results <- rbind("
        for(i <- 1 until nresult){
            analyse_file_content += "result" + i + (if (i == nresult - 1) ")\n" else ",")
        }
        analyse_file_content += "raw_df <- data.frame(results)" + "\n"
        analyse_file_content += "df <- raw_df %>% \n  " +
          "group_by(fullname, par.or.seq, technique, total.size, classes, attributes, combo) %>% \n" +
          "summarise(mean_global.time = mean(global.time),\n" +
          "mean_step1.time = mean(step1.time), \n" +
          "mean_step2.time = mean(step2.time), \n" +
          "mean_step3.time = mean(step3.time)) \n \n" +
          "df <- df[order(df$total.size), ]\n \n" +
          "ggplot(data=df, aes(x=total.size, y=mean_global.time, group=fullname, color=fullname)) + \n" +
          " \t geom_line() + geom_point() + \n" +
          " \t xlab(\"Size\") + ylab(\"Computation time (ms)\") + scale_color_discrete(name = \"Approach\") + \n" +
          " \t ggtitle(\"Computation time per size with 6 different approaches\")\n\n"

        analyse_file_content += "colors <- c(\"step1\" = \"darkred\", \"step2\" = \"steelblue\", \"step3\" = \"darkgreen\")\n\n"

        for(method <- methods) {
            val method_name =  method._1 + "." + method._2
            val name_df_method = "df." + method_name
            analyse_file_content += name_df_method + " <- subset(df, fullname == \""+ method_name + "\")\n"
            analyse_file_content += "ggplot(data=" + name_df_method + ", aes(x=total.size)) +\n" +
              "\t geom_line(aes(y=mean_step1.time, color=\"step1\")) +\n" +
              "\t geom_point(aes(y=mean_step1.time, color=\"step1\")) +\n" +
              "\t geom_line(aes(y=mean_step2.time, color=\"step2\")) +\n" +
              "\t geom_line(aes(y=mean_step3.time, color=\"step3\")) +\n" +
              "\t   labs(x = \"Size\",\n" +
              "\t        y = \"Computation time (ms)\",\n" +
              "\t        color = \"Legend\") +" +
              "\n   scale_color_manual(values = colors)" + "\n\n"
        }

        // Write content of the file
        val filename_analysis = DIR_RES_NAME + "/" + FILE_RES_NAME + "_" + TimeUtil.strLocalTime + "." + FILE_RES_ANALYSE_EXT
        FileUtil.write_content(filename_analysis, analyse_file_content)
    }

    def main(args: Array[String]) : Unit = {
        val filenames = conduct_time_experiments()
        create_analysis(filenames)
    }

}