package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.impl.TraceLinksList
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRule extends ExperimentalTransformationEngine{


    private def instantiateTracesByRule[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML],  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], npartition: Int,
     sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.seq_trace_par_apply_ByRule(tr, sm, mm, npartition, sc)
        (tls.getTargetElements , tls)
    }

    private def allSourcePatterns[SME: ClassTag, TME: ClassTag](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns.distinct

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : List[TML] = {
        sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect().toList
    }





    def execute_bystep[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext, nstep: Int = 5): (Double, List[Double]) = {
        // 1.Instantiate the trace + output element

        var t1_start : Long = 0
        var t2_start : Long  = 0
        var t3_start : Long = 0
        var t4_start : Long  = 0
        var t5_start : Long  = 0

        var t1_end : Long = 0
        var t2_end : Long  = 0
        var t3_end : Long = 0
        var t4_end : Long  = 0
        var t5_end : Long  = 0

        var tuples : RDD[List[SME]] = null
        var trace : TraceLinks[SME, TME] = null
        var elements_and_tls : (List[TME], TraceLinks[SME, TME]) = (null, null)
        var elements: List[TME] = null
        var tls: TraceLinks[SME, TME]  = null
        var tls_broad: Broadcast[TraceLinks[SME,TME]] = null
        var sps_rdd: RDD[List[SME]] = null
        var links : List[TML] = null

        val model = sc.broadcast(sm)
        val metamodel = sc.broadcast(mm)
        val transformation = sc.broadcast(tr)

        if (nstep >= 1) {
            // 1.Create the tuples
            t1_start = System.nanoTime
            tuples = sc.parallelize(allTuplesByRule(tr, sm, mm), npartition)
//            println(tuples.count() + " tuples")
            t1_end = System.nanoTime
        }

        if (nstep >= 2) {
            // 2.Instantiate tracelinks
            t2_start = System.nanoTime
            trace = new TraceLinksList(tuples.flatMap(tuple => tracePattern(transformation.value, model.value, metamodel.value, tuple)).collect)
            t2_end = System.nanoTime
        }

        if (nstep >= 3) {
            // 3.Extract info from tracelinks
            t3_start = System.nanoTime
            elements_and_tls = (trace.getTargetElements, trace)
            elements = elements_and_tls._1
//            println(elements.length + " output element")
            tls = elements_and_tls._2
            t3_end = System.nanoTime
        }

        if (nstep >= 4) {
            // 4.Broad cast tls, and assign a subpart to compute via a RDD
            t4_start = System.nanoTime
            tls_broad = sc.broadcast(tls)
            sps_rdd = sc.parallelize(allSourcePatterns(tls), npartition)
            t4_end = System.nanoTime
        }

        if (nstep >= 5) {
            // 5.Instantiate links (apply phase)
            t5_start = System.nanoTime
            links = applyTraces(transformation.value, model.value, metamodel.value, sps_rdd, tls_broad.value)
//            println(links.length + " output links")
            t5_end = System.nanoTime
        }

        // THE END

        val t1 = (t1_end - t1_start) * 1000 / 1e9d
        val t2 = (t2_end - t2_start) * 1000 / 1e9d
        val t3 = (t3_end - t3_start) * 1000 / 1e9d
        val t4 = (t4_end - t4_start) * 1000 / 1e9d
        val t5 = (t5_end - t5_start) * 1000 / 1e9d
        val time =  t1 + t2 + t3 + t4 + t5
        (time, List(t1,t2,t3,t4,t5))
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext): (Double, List[Double]) = {
        // 1.Instantiate the trace + output element

        val t1 = System.nanoTime
        val elements_and_tls =  instantiateTracesByRule(tr, sm, mm, npartition, sc)
        val elements: List[TME] = elements_and_tls._1
        val tls: TraceLinks[SME, TME] = elements_and_tls._2

        val t2 = System.nanoTime
        // 2.Broad cast tls, and assign a subpart to compute via a RDD
        val tls_broad: Broadcast[TraceLinks[SME, TME]] = sc.broadcast(tls)
        val sps_rdd: RDD[List[SME]] = sc.parallelize(allSourcePatterns(tls), npartition)
        val t3 = System.nanoTime

        // 3.Instantiate links
        val links = applyTraces(tr, sm, mm, sps_rdd, tls_broad.value)
        val t4 = System.nanoTime
        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t3_to_t4 = (t4 - t3) * 1000 / 1e9d
        val t1_to_t4 =  t1_to_t2 + t2_to_t3 + t3_to_t4
        (t1_to_t4, List(t1_to_t2, t2_to_t3, t3_to_t4))
    }

}
