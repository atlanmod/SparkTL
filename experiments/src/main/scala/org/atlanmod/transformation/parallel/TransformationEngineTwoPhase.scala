package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineTwoPhase extends ExperimentalTransformationEngine{

    private def instantiateTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML],  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.parallel_trace(tr, sm, mm, sc)
        (tls.getTargetElements , tls)
    }


    def allSourcePatternsParallel[SME, TME](tls: TraceLinks[SME, TME], sc:SparkContext) : RDD[List[SME]] =
        sc.parallelize(tls.getSourcePatterns)

    def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
        val tls_broad = sc.broadcast(tls)
        tls_broad.value.getSourcePatterns.
          flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls_broad.value))
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           sc: SparkContext)
    : (Double, List[Double]) = {
        val t1 = System.nanoTime
        val elements_and_tls = instantiateTraces(tr, sm, mm, sc)
        val t2 = System.nanoTime
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls, sc)
        val t3 = System.nanoTime

        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t1_to_t3 = (t3 - t1) * 1000 / 1e9d

        (t1_to_t3, List(t1_to_t2, t2_to_t3))
    }

}
