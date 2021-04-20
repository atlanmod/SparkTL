package org.atlanmod.transformation.twophase

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object HM_paralleltuples extends ExperimentalTransformationEngine {

    private def instantiateTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                    sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                    sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.trace_parallelTuples_HM(tr, sm, mm, sc)
        (tls.getTargetElements , tls)
    }


    def allSourcePatterns[SME, TME](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns

    private def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                              tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
        allSourcePatterns(tls).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))
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
