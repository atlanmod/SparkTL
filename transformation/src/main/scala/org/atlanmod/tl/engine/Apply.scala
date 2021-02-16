package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Eval.{evalIteratorExpr, evalOutputPatternElementExpr, evalOutputPatternLinkExpr}
import org.atlanmod.tl.engine.Instantiate.matchPattern
import org.atlanmod.tl.model._
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

object Apply{

    private def applyReferenceOnPattern[SME, SML, SMC, SMR, TME, TML](oper: OutputPatternElementReference[SME, SML, TME, TML],
                                                                      tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      sp: List[SME], iter: Int, te: TME)
    : Option[TML] = {
        evalOutputPatternLinkExpr(sm, sp, te, iter, Trace.trace(tr, sm, mm), oper)
    }


    def applyElementOnPattern[SME, SML, SMC, SMR, TME, TML](ope: OutputPatternElement[SME, SML, TME, TML],
                                                                    tr: Transformation[SME, SML, SMC, TME, TML],
                                                                    sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                    sp: List[SME], iter: Int)
    : List[TML] = {
        ope.getOutputElementReferences.flatMap(
            oper =>
                evalOutputPatternElementExpr(sm, sp, iter, ope) match {
                case Some(l) => optionToList(applyReferenceOnPattern(oper, tr, sm, mm, sp, iter, l))
                case _ => List()
            }
        )
    }


    private def applyIterationOnPattern[SME, SML, SMC, SMR, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                                      tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      sp: List[SME], iter: Int)
    : List[TML] = {
        r.getOutputPatternElements.flatMap(o => applyElementOnPattern(o, tr, sm, mm, sp, iter))
    }


    private def applyRuleOnPattern[SME, SML, SMC, SMR, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                                 tr: Transformation[SME, SML, SMC, TME, TML],
                                                                 sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                 sp: List[SME])
    : List[TML] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => applyIterationOnPattern(r, tr, sm, mm, sp, i))


    def applyPattern[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                   sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                   sp : List[SME])
    : List[TML] =
        matchPattern(tr, sm, mm, sp).flatMap(r => applyRuleOnPattern(r, tr, sm, mm, sp))


    def applyReferenceOnPatternTraces[SME, SML, SMC, SMR, TME, TML](oper: OutputPatternElementReference[SME, SML, TME, TML],
                                                                           tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML],
                                                                           sp: List[SME], iter: Int, te: TME,
                                                                           tls: TraceLinks[SME, TME])
    : Option[TML] = {
        evalOutputPatternLinkExpr(sm, sp, te, iter, tls, oper)
    }

    def applyElementOnPatternTraces[SME, SML, SMC, SMR, TME, TML](ope: OutputPatternElement[SME, SML, TME, TML],
                                                                          tr: Transformation[SME, SML, SMC, TME, TML],
                                                                          sm: Model[SME, SML],
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
                                                               sm: Model[SME, SML], sp: List[SME], iter: Int,
                                                               tls: TraceLinks[SME, TME])
    : List[TML] =
        r.getOutputPatternElements.flatMap(o => applyElementOnPatternTraces(o, tr, sm, sp, iter, tls))

    def applyRuleOnPatternTraces[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                          tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], sp: List[SME],
                                                          tls: TraceLinks[SME, TME])
    : List[TML] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => applyIterationOnPatternTraces(r, tr, sm, sp, i, tls))

    def applyPatternTraces[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                         sm: Model[SME, SML], mm: Metamodel[SME,SML,SMC,SMR],
                                                         sp: List[SME],  tls: TraceLinks[SME, TME])
    : List[TML] =
        matchPattern(tr, sm, mm, sp).flatMap(r => applyRuleOnPatternTraces(r, tr, sm, sp, tls))

}
