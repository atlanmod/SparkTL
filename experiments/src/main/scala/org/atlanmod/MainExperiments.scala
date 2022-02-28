package org.atlanmod

import org.atlanmod.engine.CaseUtils.{IMDBIDENTITY, getMetamodel}
import org.atlanmod.engine.{ArgsUtils, CaseUtils, ExperimentalEngine, ParamUtils}
import org.atlanmod.tl.engine.Parameters
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}

object MainExperiments {

    final val DEFAULT_TRANSFORMATION = IMDBIDENTITY
    final val DEFAULT_SIZE: Int = 100

    def getMetamodelFromArgs(args: Array[String]): DynamicMetamodel[DynamicElement, DynamicLink] =
        getMetamodel(ArgsUtils.getOrElse(args, "-case", DEFAULT_TRANSFORMATION, a => a))

    def ConfigModel(args: Array[String]): (String, Int, List[String]) = {
        if (ArgsUtils.has(args, "-file") || ArgsUtils.has(args, "-files"))
            ("files", 0, ArgsUtils.getAll(args, "-file"))
        else
            ("size", ArgsUtils.getOrElse(args, "-size", DEFAULT_SIZE, a => a.toInt), List())
    }

    def main(args: Array[String]): Unit = {
        val config: Parameters.Config = ParamUtils.getConfig(args)
        val case_ : String = ArgsUtils.getOrElse(args, "-case", DEFAULT_TRANSFORMATION, a => a)
        val transformation = CaseUtils.getTransformation(case_, config)
        val metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = getMetamodelFromArgs(args)
        val configModel: (String, Int, List[String]) = ConfigModel(args)
        val model: DynamicModel = CaseUtils.getModel(metamodel, configModel._1, configModel._2, configModel._3)
        val res = ExperimentalEngine.executeWithConfig(transformation, model, metamodel, config)

        val execution_result = new ExecutionResult(case_, model, res._1, res._2, config)

        println(execution_result.csv_header)
        println(execution_result.csv_line)
    }

}
