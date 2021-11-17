package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Eval.{evalIteratorExpr, evalOutputPatternElementExpr, evalOutputPatternLinkExpr}
import org.atlanmod.tl.engine.Instantiate.matchPattern
import org.atlanmod.tl.model._
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

object Apply{

    def applyReferenceOnPatternTraces[SME, SML, SMC, SMR, TME, TML](oper: OutputPatternElementReference[SME, SML, SMC, TME, TML],
                                                                           tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML, SMC],
                                                                           sp: List[SME], iter: Int, te: TME,
                                                                           tls: TraceLinks[SME, TME])
    : Option[TML] = {
        evalOutputPatternLinkExpr(sm, sp, te, iter, tls, oper)
    }

    def applyElementOnPatternTraces[SME, SML, SMC, SMR, TME, TML](ope: OutputPatternElement[SME, SML, SMC, TME, TML],
                                                                          tr: Transformation[SME, SML, SMC, TME, TML],
                                                                          sm: Model[SME, SML, SMC],
                                                                          sp: List[SME], iter: Int,
                                                                          tls: TraceLinks[SME, TME])
    : List[TML] =
        ope.getOutputElementReferences.flatMap(oper =>
            evalOutputPatternElementExpr(sm, sp, iter, ope) match {
                case Some(l) => optionToList(applyReferenceOnPatternTraces(oper, tr, sm, sp, iter, l, tls))
                case None => List()
            }
        )

    def applyIterationOnPatternTraces[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                               tr: Transformation[SME, SML, SMC, TME, TML],
                                                               sm: Model[SME, SML, SMC], sp: List[SME], iter: Int,
                                                               tls: TraceLinks[SME, TME])
    : List[TML] =
        r.getOutputPatternElements.flatMap(o => applyElementOnPatternTraces(o, tr, sm, sp, iter, tls))

    def applyRuleOnPatternTraces[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                          tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML, SMC], sp: List[SME],
                                                          tls: TraceLinks[SME, TME])
    : List[TML] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => applyIterationOnPatternTraces(r, tr, sm, sp, i, tls))

    def applyPatternTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                         sm: Model[SME, SML, SMC], mm: Metamodel[SME,SML,SMC,SMR],
                                                         sp: List[SME],  tls: TraceLinks[SME, TME])
    : List[TML] =
        matchPattern(tr, sm, mm, sp).flatMap(r => applyRuleOnPatternTraces(r, tr, sm, sp, tls))

}
