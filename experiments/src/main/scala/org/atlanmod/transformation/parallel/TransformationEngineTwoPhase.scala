package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineTwoPhase extends ExperimentalTransformationEngine{

    private def instantiateTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML],  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], npartition: Int,
     sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.parallel_trace(tr, sm, mm, npartition, sc)
        (tls.getTargetElements , tls)
    }


    def allSourcePatternsParallel[SME, TME](tls: TraceLinks[SME, TME], sc:SparkContext) : RDD[List[SME]] =
        sc.parallelize(tls.getSourcePatterns)

    def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      tls_rdd: RDD[List[SME]], tls_full: TraceLinks[SME, TME],
                                                                      sc: SparkContext)
    : Array[TML] = {
        tls_rdd.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls_full)).collect
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext): (Double, List[Double]) = {
        val t1 = System.nanoTime

        // 1.Instantiate the trace + output element
        val elements_and_tls = instantiateTraces(tr, sm, mm, npartition, sc)
        val t2 = System.nanoTime
        val elements: List[TME] = elements_and_tls._1
        val tls: TraceLinks[SME, TME] = elements_and_tls._2

        // 2.Broad cast tls, and assign a subpart to compute via a RDD
        val tls_rdd: RDD[List[SME]] = sc.parallelize(tls.getSourcePatterns, npartition)
        val tls_broad: Broadcast[TraceLinks[SME, TME]] = sc.broadcast(tls)
        val tls_full = tls_broad.value
        val t3 = System.nanoTime

        // 3.Instantiate links
        val links = applyTraces(tr, sm, mm, tls_rdd, tls_full, sc)
        val t4 = System.nanoTime

        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t3_to_t4 = (t4 - t3) * 1000 / 1e9d
        val t1_to_t4 = (t4 - t1) * 1000 / 1e9d

        (t1_to_t4, List(t1_to_t2, t2_to_t3, t3_to_t4))
    }

}
