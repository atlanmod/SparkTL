package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.{DefaultModel, TraceLinksMap}

import scala.reflect.ClassTag

object TransformationSequential {

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: Iterable[List[SME]], tls: TraceLinks[SME, TME])
    : Iterable[TML] = sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag, TMC: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : Model[TME, TML] = {

        val tls : TraceLinks[SME, TME] =
            new TraceLinksMap(
                allTuplesByRule(tr, sm, mm).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).groupBy(tl => tl.getSourcePattern)
            )
        val elements = tls.getTargetElements
        val links = applyTraces(tr, sm, mm, tls.getSourcePatterns, tls)
        new DefaultModel(elements, links) // Output format
    }



}