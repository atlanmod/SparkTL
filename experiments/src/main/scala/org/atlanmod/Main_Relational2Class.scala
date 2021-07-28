package org.atlanmod

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.class2relational.model.relationalmodel.{RelationalMetamodel, RelationalModel, RelationalTable}
import org.atlanmod.class2relational.transformation.dynamic.Relational2Class
import org.atlanmod.transformation.parallel.{TransformationEngineImpl, TransformationEngineTwoPhase, TransformationEngineTwoPhaseByRule}
import org.atlanmod.util.R2CUtil

object Main_Relational2Class {

    val TUPLES_MODE_DEFAULT = "by_rule" // or "full"
    var tuples_mode: String = TUPLES_MODE_DEFAULT

    val PIVOT_COMPLEXITY_DEFAULT = "simple" // or "full"
    var pivot_complexity: String = PIVOT_COMPLEXITY_DEFAULT

    final val DEFAULT_SIZE: Int = 10
    var model_size: Int = DEFAULT_SIZE

    final val DEFAULT_NCORE: Int = 1
    var ncore: Int = DEFAULT_NCORE

    final val DEFAULT_NSTEP: Int = 5
    var nstep: Int = DEFAULT_NSTEP

    final val DEFAULT_NEXECUTOR: Int = 1
    var nexecutor: Int = DEFAULT_NEXECUTOR

    final val DEFAULT_NPARTITION: Int = 1 * 4
    var npartition: Int = DEFAULT_NPARTITION

    final val DEFAULT_SLEEPING_GUARD: Int = 0
    var sleeping_guard: Int = DEFAULT_SLEEPING_GUARD

    final val DEFAULT_SLEEPING_INSTANTIATE: Int = 0
    var sleeping_instantiate: Int = DEFAULT_SLEEPING_INSTANTIATE

    final val DEFAULT_SLEEPING_APPLY: Int = 0
    var sleeping_apply: Int = DEFAULT_SLEEPING_APPLY


    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-size" :: size :: args => {
                model_size = size.toInt
                parseArgs(args)
            }
            case "-core" :: core :: args => {
                ncore = core.toInt
                parseArgs(args)
            }
            case "-sleep_guard" :: sleep :: args => {
                sleeping_guard = sleep.toInt
                parseArgs(args)
            }
            case "-sleep_instantiate" :: sleep :: args => {
                sleeping_instantiate = sleep.toInt
                parseArgs(args)
            }
            case "-sleep_apply" :: sleep :: args => {
                sleeping_apply = sleep.toInt
                parseArgs(args)
            }
            case "-executor" :: executor :: args =>{
                nexecutor = executor.toInt
                parseArgs(args)
            }
            case "-partition" :: partition :: args =>{
                npartition = partition.toInt
                parseArgs(args)
            }
            case "-mode" :: mode :: args =>{
                assert(mode.equals("by_rule") || mode.equals("full") || mode.equals("simple"))
                tuples_mode = mode
                parseArgs(args)
            }
            case "-step" :: step :: args =>{
                nstep = step.toInt
                parseArgs(args)
            }
            case "-pivot" :: pivot :: args =>{
                assert(pivot.equals("simple") || pivot.equals("complex") || pivot.equals("n2"))
                pivot_complexity = pivot
                parseArgs(args)
            }
            case _ :: args => parseArgs(args)
            case List() =>
        }
    }

    def getContext(): SparkContext = {
        val conf = new SparkConf()
//        if (conf.getExecutorEnv.isEmpty) {
          // conf.setMaster("local[" + (DEFAULT_NEXECUTOR * DEFAULT_NCORE) + "]")
            //conf.setAppName("Relational2Class")
//        }
        new SparkContext(conf)
    }

    def main(args: Array[String]): Unit = {
       try {
           parseArgs(args.toList)
           //println(args)
           val sc = getContext()
           var foo_pivot: (RelationalModel, RelationalTable, RelationalTable) => Boolean = null
           var foo_notpivot: (RelationalModel, RelationalTable) => Boolean = null

           if (pivot_complexity.equals("simple")) {
                foo_pivot = Relational2Class.isPivot
                foo_notpivot = Relational2Class.isNotPivot
           }
           if (pivot_complexity.equals("complex")) {
               foo_pivot = Relational2Class.isPivot_complex
               foo_notpivot = Relational2Class.isNotPivot_complex
           }
           if (pivot_complexity.equals("n2")) {
               foo_pivot = Relational2Class.isPivot_n2
               foo_notpivot = Relational2Class.isNotPivot_n2
            }

            val transformation = org.atlanmod.class2relational.transformation.dynamic.Relational2Class.relational2class(sleeping_guard, sleeping_instantiate, sleeping_apply,foo_pivot,foo_notpivot)
            val input_model = R2CUtil.get_model_from_n_patterns(model_size)
            val input_metamodel = RelationalMetamodel.metamodel

            var line = List(input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition,
                sleeping_guard,sleeping_instantiate,sleeping_apply).mkString(",")
//        println(line)

        var res: (Double, List[Double]) = null
        if (tuples_mode == "by_rule")
            res = TransformationEngineTwoPhaseByRule.execute_bystep(transformation, input_model, input_metamodel, npartition, sc, nstep)
        if (tuples_mode == "full")
            res = TransformationEngineTwoPhase.execute(transformation, input_model, input_metamodel, npartition, sc)
        if (tuples_mode == "simple")
            res = TransformationEngineImpl.execute(transformation, input_model, input_metamodel, npartition, sc)


        //println("implem,element,link,executor,core,partition,sleeping_guard,sleeping_instantiate,sleeping_apply,total_time,time_tuples,time_instantiate,time_extract,time_broadcast,time_apply")
        line = List(tuples_mode,input_model.allModelElements.length, input_model.allModelLinks.length, nexecutor, ncore, npartition,
            sleeping_guard,sleeping_instantiate,sleeping_apply).mkString(",")
        println(line + "," + res._1 + "," + res._2.mkString(","))

       } catch {
           case e: Exception => println(e.getMessage)
       }
    }
}
