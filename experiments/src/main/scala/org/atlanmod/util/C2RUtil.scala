package org.atlanmod.util

import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.apache.spark.SparkContext
import org.atlanmod.model.classmodel.ClassModel
import org.atlanmod.tl.model.{Model, Transformation}
import org.atlanmod.tl.util.SparkUtils

import scala.collection.mutable

object C2RUtil {

    def size_model(size: (Int, Int, Int)): Int = size._1 + size._1 * size._2 + size._1 * size._3

    class ResultC2R (par_seq: String, name_meth: String, ncore: Int, nclass: Int, nattribute: Int, nderived: Int, time: (Double, List[Double])) {
        def make_csv_line(): String = {
            (List(par_seq + "." + name_meth,
                par_seq, ncore, name_meth,
                size_model((nclass, nattribute, nderived)), nclass, nattribute, nderived,
                nclass + "_" + nattribute + "_" + nderived,
                time._1) ++ time._2).mkString(",")
        }
    }

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

    def get_methods(): List[(String, String, transformation_function)] = {
        val res : List[(String, String, transformation_function)] =
            List(
//                ("seq", "simple", (tr, m, mm, sc) =>  atlanmod.transformation.sequential.TransformationEngineImpl.execute(tr, m, mm, sc)),
//                ("par", "simple", (tr, m, mm, sc) =>  atlanmod.transformation.parallel.TransformationEngineImpl.execute(tr, m, mm, sc)),
//                ("seq", "byrule", (tr, m, mm, sc) =>  atlanmod.transformation.sequential.TransformationEngineByRule.execute(tr, m, mm, sc)),
//                ("par", "byrule", (tr, m, mm, sc) =>  atlanmod.transformation.parallel.TransformationEngineByRule.execute(tr, m, mm, sc)),
//                ("seq", "twophase", (tr, m, mm, sc) =>  atlanmod.transformation.sequential.TransformationEngineTwoPhase.execute(tr, m, mm, sc)),
                ("par", "HM_allparallel", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_allparallel.execute(tr, m, mm, sc)),
                ("seq", "HM_noparallelism", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_noparallelism.execute(tr, m, mm, sc)),
                ("par", "HM_parallelsm", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_parallelsp.execute(tr, m, mm, sc)),
                ("par", "HM_parallelsp_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_parallelsp_paralleltuples.execute(tr, m, mm, sc)),
                ("par", "HM_paralleltrace", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_paralleltrace.execute(tr, m, mm, sc)),
                ("par", "HM_paralleltrace_parallelsp", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_parallelsp.execute(tr, m, mm, sc)),
                ("par", "HM_paralleltrace_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_paralleltrace_paralleltuples.execute(tr, m, mm, sc)),
                ("par", "HM_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.HM_paralleltuples.execute(tr, m, mm, sc)),
                ("par", "List_allparallel", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_allparallel.execute(tr, m, mm, sc)),
                ("seq", "List_noparallelism", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_noparallelism.execute(tr, m, mm, sc)),
                ("par", "List_parallelsm", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_parallelsp.execute(tr, m, mm, sc)),
                ("par", "List_parallelsp_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_parallelsp_paralleltuples.execute(tr, m, mm, sc)),
                ("par", "List_paralleltrace", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_paralleltrace.execute(tr, m, mm, sc)),
                ("par", "List_paralleltrace_parallelsp", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_parallelsp.execute(tr, m, mm, sc)),
                ("par", "List_paralleltrace_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_paralleltrace_paralleltuples.execute(tr, m, mm, sc)),
                ("par", "List_paralleltuples", (tr, m, mm, sc) =>  org.atlanmod.transformation.twophase.List_paralleltuples.execute(tr, m, mm, sc)),
            )
        res
    }

//    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1, nattributemv: Int = 0): ClassModel = {
//        var elements : List[ClassElement] = List()
//        var links : List[ClassLink] = List()
//        for(i <- 1 to nclass){
//            val cc = new ClassClass(i.toString, "")
//            elements = cc :: elements
//            var cc_attributes : List[ClassAttribute] = List()
//            for (j <- 1 to nattribute) {
//                val ca = new ClassAttribute(i.toString + "." + j.toString, "")
//                elements = ca :: elements
//                links = new AttributeToClass(ca, cc) :: links
//                cc_attributes = ca :: cc_attributes
//            }
//            for (j <- (1 + nattribute) to (nattributemv + nattribute)) {
//                val ca = new ClassAttribute(i.toString + "." + j.toString, "")
//                elements = ca :: elements
//                links = new AttributeToClass(ca, cc) :: links
//                cc_attributes = ca :: cc_attributes
//            }
//            links = new ClassToAttributes(cc, cc_attributes) :: links
//
//        }
//        new ClassModel(elements, links)
//    }

    def apply_transformation(tr_foo: transformation_function, tr: transformation_type,
                             sm: source_model, mm: source_metamodel, sc: SparkContext): (Double, List[Double]) = {
        tr_foo(tr, sm, mm, sc)
    }

    def apply_transformations(tr_foo: transformation_function, tr: transformation_type,
                              sm: source_model, mm: source_metamodel, sc: SparkContext, times: Int)
    : List[(Double, List[Double])] = {
        var res: List[(Double, List[Double])] = List()
        for(_ <- 1 to times) {
            res = apply_transformation(tr_foo, tr, sm, mm, sc) :: res
        }
        res.reverse
    }


    def apply_transformations_methods(methods: List[(String, String, transformation_function)], tr: transformation_type,
                              sm: source_model, mm: source_metamodel, sc: SparkContext, times: Int)
    : mutable.HashMap[(String, String), List[(Double, List[Double])]]
    = {
        val res = new mutable.HashMap[(String, String), List[(Double, List[Double])]]
        for(method <- methods)
            res.put((method._1, method._2), apply_transformations(method._3, tr, sm, mm, sc, times))
        res
    }

//    def running_test(methods: List[(String, String, transformation_function)], tr: transformation_type, mm: source_metamodel,
//                     times: Int, ncore: Int, nclass: Int, nattribute: Int, nderived: Int) : List[ResultC2R] = {
//        var res : List[ResultC2R] = List()
//        val sc = if (ncore != 0) SparkUtils.context(ncore) else null
//        val sm = dynamic_simple_model(nclass, nattribute, nderived)
//
//        val methods_to_apply = methods.filter(m => m._1 == (if (ncore == 0) "seq" else "par"))
//
//        val times_for_methods : mutable.HashMap[(String, String), List[(Double, List[Double])]] =
//            apply_transformations_methods(methods_to_apply, tr, sm, mm, sc, times)
//
//        for (method <- times_for_methods.keySet) {
//            val par_seq = method._1
//            val name_method = method._2
//            for (times <- times_for_methods.get(method)){
//                for (time <- times){
//                    res = new ResultC2R(par_seq, name_method, ncore, nclass, nattribute, nderived, time) :: res
//                }
//            }
//        }
//
//        if (ncore != 0) sc.stop()
//        res
//    }

//    def running_test_csv(methods: List[(String, String, transformation_function)], tr: transformation_type, mm: source_metamodel,
//                     times: Int, ncore: Int, nclass: Int, nattribute: Int, nderived: Int) : List[String] = {
//        running_test(methods, tr, mm, times, ncore, nclass, nattribute, nderived).map(r => r.make_csv_line())
//    }

    def make_vector_results(csvfiles: List[String]): String = {
        csvfiles.map(f => "read.csv(file=\""+f+"\", colClasses = colTypes)").mkString(",")
    }

    def make_rmd_content(csvfiles : List[String]) : String = {
        val content = "# Environment setup\n\n" +
          "## Libraries\n\n" +
          "```{r include=FALSE}\n" +
          "library(ggplot2)\nlibrary(dplyr)\nlibrary(Rmpfr)\n" +
          "```\n\n" +
          "## Colors and col types\n\n" +
          "```{r}\ncolors <- c(\"step1\" = \"darkred\", \"step2\" = \"steelblue\", \"step3\" = \"darkgreen\")\n" +
          "colTypes <- c(\"character\", \"character\", \"numeric\", \"character\", \"numeric\",\"numeric\",\"numeric\",\"character\",\"numeric\",\"numeric\",\"numeric\",\"numeric\")\n" +
          "```\n\n" +
          "# Data\n\n" +
          "## Results (csv)\n\n" +
          "```{r}\n" +
          "results <- rbind(" + make_vector_results(csvfiles) + ")\n\n"+
          "```\n\n" +
          "## Clean dataset\n\n" +
          "```{r}\n" +
          "raw_df <- data.frame(results)\n" +
          "df <- raw_df %>% \n" +
          "  group_by(fullname, par.or.seq, technique, total.size, classes, attributes, combo, ncore) %>% \n" +
          "  summarise(mean_global.time = mean(global.time),\n" +
          "mean_step1.time = mean(step1.time), \n" +
          "\tmean_step2.time = mean(step2.time), \n" +
          "\tmean_step3.time = mean(step3.time)) \n\n" +
          "df <- df[order(df$total.size), ]\n" +
          "```\n\n\n" +
          "# Scalability\n\n## Horizontal scalability\n\n" +
          "```{r}\n" +
          "horizontal_core <- function(data, nc) {\n" +
          "  df.ncore <- subset(data, ncore == nc)\n\n" +
          "  if (nc == 0) {\n    " +
          "\ttitle <- \"Computation time per size with 3 different sequential approaches\"\n  " +
          "  } else { \n  " +
          "\ttitle <- paste(\"Computation time per size with 3 different parallel approaches with \", toString(nc), \" cores\", sep=\"\")\n " +
          "  }\n\n  " +
          "  ggplot(data = df.ncore, aes(x=total.size, y=mean_global.time, group=fullname, color=fullname)) +\n" +
          "  geom_line() + geom_point() +\n" +
          "  xlab(\"Size\") + ylab(\"Computation time (ms)\") + scale_color_discrete(name = \"Approach\") +\n" +
          "  ggtitle(title)\n}\n\n" +
          "for(core in unique(df$ncore)){\n  print(horizontal_core(df, core))\n}\n" +
          "```\n" +
          "## Vertical scalability\n\n" +
          "```{r}\n" +
          "vertical_core <- function(data, method, sizes) {\n" +
          "  df.method <- subset(data, technique==method)\n" +
          "  df.method.vertical <- subset(df.method, (combo %in% sizes))\n" +
          "  title <- paste(\"Computation time per number of core for \", method, \" approach\", sep = \"\")\n" +
          "  ggplot(data =df.method.vertical, aes(x=ncore, y=mean_global.time, group=combo, color=combo)) +\n" +
          "    geom_line() + geom_point() +\n" +
          "    xlab(\"NCore\") + ylab(\"Computation time (ms)\") + scale_color_discrete(name = \"Dataset\") +\n" +
          "    ggtitle(title)\n}\n\n" +
          " if (length(unique(df$combo)) < 8){\n" +
          "  sizes <- sample (unique(df$combo), size=length(unique(df$combo)), replace =F)" +
          "}else{\n" +
          "  sizes <- sample (unique(df$combo), size=8, replace =F)\n" +
          "}\n\n" +
          "for(method in unique(df$technique)){\n  print(vertical_core(df, method, sizes))\n}\n" +
          "```\n\n" +
          "# By step\n\n" +
          "```{r}\n" +
          "by_step <- function(data, nc, method) {\n" +
          "  df.ncore <- subset(data, ncore == nc)\n" +
          "  if (nc == 0){\n    df.method.ncore <- subset(df.ncore, fullname == paste(\"seq\", method, sep=\".\"))\n" +
          "    title <- paste(\"Computation time for each step with the \", method, \" approach on sequential\", sep=\"\")\n" +
          "  } else {\n    df.method.ncore <- subset(df.ncore, fullname == paste(\"par\", method, sep=\".\"))\n" +
          "    title <- paste(\"Computation time for each step with the \", method, \" approach on parallel (\", nc , \" cores)\" , sep=\"\")\n" +
          "  }\n \n" +
          "  ggplot(data=df.method.ncore, aes(x=total.size)) +\n  geom_line(aes(y=mean_step1.time, color=\"step1\"))+\n" +
          "  geom_line(aes(y=mean_step2.time, color=\"step2\"))+\n  geom_line(aes(y=mean_step3.time, color=\"step3\"))+\n" +
          "  labs(x = \"Size\",  y = \"Computation time (ms)\", color = \"Legend\") + \n  scale_color_manual(values = colors) +\n" +
          "  ggtitle(title)\n}\n\nfor(core in unique(df$ncore)){\n  for(method in unique(df$technique)){\n" +
          "    print(by_step(df, core, method))\n  }\n}\n\n" +
          "```"
        content
    }
}
