package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineOnePhaseByRule extends ExperimentalTransformationEngine{


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
//        val tls_broad: Broadcast[TraceLinks[SME, TME]] = sc.broadcast(tls)
//        val sps_rdd: RDD[List[SME]] = sc.parallelize(allSourcePatterns(tls), npartition)
        val t3 = System.nanoTime

        // 3.Instantiate links
//        val links = applyTraces(tr, sm, mm, sps_rdd, tls_broad.value)
        val t4 = System.nanoTime
        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t3_to_t4 = (t4 - t3) * 1000 / 1e9d
        val t1_to_t4 =  t1_to_t2 + t2_to_t3 + t3_to_t4
        (t1_to_t4, List(t1_to_t2, 0,0))
    }

}
