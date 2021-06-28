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

        var t0 : Long = 0
        var t1 : Long = 0
        var t2 : Long  = 0
        var t3 : Long = 0
        var t4 : Long  = 0
        var t5 : Long  = 0

        var tuples : RDD[List[SME]] = null
        var trace : TraceLinks[SME, TME] = null
        var elements_and_tls : (List[TME], TraceLinks[SME, TME]) = (null, null)
        var elements: List[TME] = null
        var tls: TraceLinks[SME, TME]  = null
        var tls_broad: Broadcast[TraceLinks[SME,TME]] = null
        var sps_rdd: RDD[List[SME]] = null
        var links : List[TML] = null

        if (nstep >= 1) {
            // 1.Create the tuples
            t0 = System.nanoTime
            tuples = sc.parallelize(allTuplesByRule(tr, sm, mm), npartition)
        }

        if (nstep >= 2) {
            // 2.Instantiate tracelinks
            t1 = System.nanoTime
            trace = new TraceLinksList(tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect)
        }

        if (nstep >= 3) {
            // 3.Extract info from tracelinks
            t2 = System.nanoTime
            elements_and_tls = (trace.getTargetElements, trace)
            elements = elements_and_tls._1
            tls = elements_and_tls._2
        }

        if (nstep >= 4) {
            // 4.Broad cast tls, and assign a subpart to compute via a RDD
            t3 = System.nanoTime
            tls_broad = sc.broadcast(tls)
            sps_rdd = sc.parallelize(allSourcePatterns(tls), npartition)
        }

        if (nstep >= 5) {
            // 5.Instantiate links (apply phase)
            t4 = System.nanoTime
            links = applyTraces(tr, sm, mm, sps_rdd, tls_broad.value)
        }
        // END
        t5 = System.nanoTime


        val t0_to_t1 = (t1 - t0) * 1000 / 1e9d
        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t3_to_t4 = (t4 - t3) * 1000 / 1e9d
        val t4_to_t5 = (t5 - t4) * 1000 / 1e9d
        val t0_to_t5 =  t0_to_t1 + t1_to_t2 + t2_to_t3 + t3_to_t4 + t4_to_t5
        (t0_to_t5, List(t0_to_t1, t1_to_t2, t2_to_t3, t3_to_t4, t4_to_t5))
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
