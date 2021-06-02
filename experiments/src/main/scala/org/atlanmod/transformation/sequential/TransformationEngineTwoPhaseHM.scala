package org.atlanmod.transformation.sequential

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.{Apply, Trace}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseHM extends ExperimentalTransformationEngine {
    private def instantiateTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls = Trace.trace_HM(tr, sm, mm)
        (tls.getTargetElements, tls)
    }

    def allSourcePatterns[SME, TME](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns

    private def applyTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: TraceLinks[SME, TME])
    : List[TML] =
        allSourcePatterns(tls).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    override def execute[SME:ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                    npartition: Int = 0, sc: SparkContext = null)
    : (Double, List[Double]) = {
        val t1 = System.nanoTime
        val elements_and_tls = instantiateTraces(tr, sm, mm)
        val t2 = System.nanoTime
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2



        val links = applyTraces(tr, sm, mm, tls)
        val t3 = System.nanoTime

        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t1_to_t3 = (t3 - t1) * 1000 / 1e9d

        (t1_to_t3, List(t1_to_t2, t2_to_t3))
    }
}
