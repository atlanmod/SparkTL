package org.atlanmod.tl.engine.sequential

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.{Apply, Trace, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

object TransformationEngineTwoPhase extends TransformationEngine {

    private def instantiateTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls = Trace.trace(tr, sm, mm)
        (tls.getTargetElements, tls)
    }

    def allSourcePatterns[SME, TME](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns.distinct

    private def applyTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: TraceLinks[SME, TME])
    : List[TML] = {
        allSourcePatterns(tls).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int = 0, sc: SparkContext = null,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces(tr, sm, mm)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls)
        makeModel(elements, links)
    }
}
