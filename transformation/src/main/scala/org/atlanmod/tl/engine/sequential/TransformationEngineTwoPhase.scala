package org.atlanmod.tl.engine.sequential

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.engine.{Apply, Trace, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLink, Transformation}

import scala.reflect.ClassTag

object TransformationEngineTwoPhase extends TransformationEngine {

    private def instantiateTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : (List[TME], List[TraceLink[SME, TME]]) = {
        val tls = Trace.trace(tr, sm, mm)
        (tls.map(tl => tl.getTargetElement), tls)
    }

    private def applyTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: List[TraceLink[SME, TME]])
    : List[TML] =
        allTuples(tr, sm).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    private def executeTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                            sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces(tr, sm, mm)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls)
        class tupleTModel(elements: List[TME], links: List[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements

            override def allModelLinks: List[TML] = links
        }
        new tupleTModel(elements, links)
    }

    override def execute[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           sc: SparkContext = null)
    : Model[TME, TML] = {
        executeTraces(tr, sm, mm)
    }
}