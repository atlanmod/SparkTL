package org.atlanmod.util

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.tl.model.{Model, Transformation}
import org.atlanmod.tl.util.SparkUtils

import scala.collection.mutable
import scala.language.postfixOps

object TransformationUtil {
//
//    class ResultC2R (par_seq: String, name_meth: String, ncore: Int, nclass: Int, nattribute: Int, nderived: Int, time: (Double, List[Double])) {
//        def make_csv_line(): String = {
//            (List(par_seq + "." + name_meth,
//                par_seq, ncore, name_meth,
//                size_model((nclass, nattribute, nderived)), nclass, nattribute, nderived,
//                nclass + "_" + nattribute + "_" + nderived,
//                time._1) ++ time._2).mkString(",")
//        }
//    }

    type SME = DynamicElement
    type SML = DynamicLink
    type SMC = String
    type SMR = String
    type TME = DynamicElement
    type TML = DynamicLink

    type transformation_type = Transformation[SME, SML, SMC, TME, TML]
    type source_model = DynamicModel
    type source_metamodel = DynamicMetamodel[DynamicElement, DynamicLink]
    type target_model = Model[DynamicElement, DynamicLink]

    type transformation_function = (transformation_type, source_model, source_metamodel, Int, SparkContext) => (Double, List[Double])

    def get_methods(method: String = "all", mode: String="both"): List[(String, String, transformation_function)] = {
        val res : List[(String, String, transformation_function)] =
            List(
                ("seq", "simple", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineImpl.execute(tr, m, mm, n, sc)),
                ("par", "simple", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineImpl.execute(tr, m, mm,  n, sc)),
                ("seq", "byrule", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineByRule.execute(tr, m, mm,  n, sc)),
                ("par", "byrule", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineByRule.execute(tr, m, mm, n, sc)),
                ("seq", "two_phase_HM", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineTwoPhaseHM.execute(tr, m, mm,  n, sc)),
                ("seq", "two_phase", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.sequential.TransformationEngineTwoPhase.execute(tr, m, mm,  n, sc)),
                ("par", "two_phase", (tr, m, mm, n, sc) =>  org.atlanmod.transformation.parallel.TransformationEngineTwoPhase.execute(tr, m, mm,  n, sc))
            )

        if (method.equals("all") && mode.equals("both"))
            res
        else if (!method.equals("all") && mode.equals("both"))
            res.filter(m => m._2.contains(method))
        else if (method.equals("all") && !mode.equals("both"))
            res.filter(m => m._1.equals(mode))
        else
            res.filter(m => m._2.contains(method)).filter(m => m._1.equals(mode))
    }

    def apply_transformation(tr_foo: transformation_function, tr: transformation_type,
                             sm: source_model, mm: source_metamodel, npartition: Int, sc: SparkContext): (Double, List[Double]) = {
        val broadcasted_tr = sc.broadcast(tr)
        val broadcaster_sm = sc.broadcast(sm)
        val broadcaster_mm = sc.broadcast(mm)
        tr_foo(broadcasted_tr.value, broadcaster_sm.value, broadcaster_mm.value, npartition, sc)
    }

    def apply_transformations(tr_foo: transformation_function, tr: transformation_type,
                              sm: source_model, mm: source_metamodel, npartition: Int, sc: SparkContext, times: Int,
                              print_screen: Boolean = false)
    : List[(Double, List[Double])] = {
        var res: List[(Double, List[Double])] = List()
        for(_ <- 1 to times) {
            val time = apply_transformation(tr_foo, tr, sm, mm, npartition, sc)
            if (print_screen) print(time._1 + "ms, ")
            res = time :: res
        }
        res.reverse
    }

    def apply(methods: List[(String, String, transformation_function)],
              transformation: Transformation[TME, SML, String, TME, SML], model: source_model,
              metamodel: DynamicMetamodel[DynamicElement, DynamicLink], times: Int, ncore: Int,
              print_screen: Boolean = false)
    : mutable.HashMap[(String, String), List[(Double, List[Double])]] = {
        val sequential = ncore == 0
        val sc = if (!sequential) SparkUtils.context(4) else null
        val res = new mutable.HashMap[(String, String), List[(Double, List[Double])]]
        for(method <- methods){
            if((sequential & method._1.equals("seq")) | (!sequential & method._1.equals("par"))) {
                if(print_screen) print("Method: "+ (method._1, method._2)+ " => ")
                res.put(
                    (method._1, method._2),
                    apply_transformations(method._3, transformation, model, metamodel, ncore * 4, sc, times, print_screen)
                )
            }
        }
        if (!sequential) sc.stop()
        res
    }

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
          "colTypes <- c(\"character\", \"character\", \"numeric\", \"character\", \"numeric\",\"numeric\",\"numeric\"," +
          "\"numeric\",\"character\",\"numeric\",\"numeric\",\"numeric\",\"numeric\")\n" +
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
          "  group_by(fullname, parseq, ncore, method, total_size, number_classes, number_attributes, number_multivalued, combo) %>% \n" +
          "  summarise(mean_global.time = mean(global_time),\n" +
          "mean_step1.time = mean(step1_time), \n" +
          "\tmean_step2.time = mean(step2_time), \n" +
          "\tmean_step3.time = mean(step3_time)) \n\n" +
          "df <- df[order(df$total_size), ]\n" +
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
          "  ggplot(data = df.ncore, aes(x=total_size, y=mean_global_time, group=fullname, color=fullname)) +\n" +
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
          "  ggplot(data=df.method.ncore, aes(x=total_size)) +\n  geom_line(aes(y=mean_step1.time, color=\"step1\"))+\n" +
          "  geom_line(aes(y=mean_step2.time, color=\"step2\"))+\n  geom_line(aes(y=mean_step3.time, color=\"step3\"))+\n" +
          "  labs(x = \"Size\",  y = \"Computation time (ms)\", color = \"Legend\") + \n  scale_color_manual(values = colors) +\n" +
          "  ggtitle(title)\n}\n\nfor(core in unique(df$ncore)){\n  for(method in unique(df$technique)){\n" +
          "    print(by_step(df, core, method))\n  }\n}\n\n" +
          "```"
        content
    }
}
