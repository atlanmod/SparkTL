package org.atlanmod.tl.sequential

import org.atlanmod.tl.sequential.Eval.{evalIteratorExpr, evalOutputPatternElementExpr, evalOutputPatternLinkExpr}
import org.atlanmod.tl.sequential.Instantiate.matchPattern
import org.atlanmod.tl.sequential.Trace.trace
import org.atlanmod.tl.sequential.spec.{Metamodel, Model, OutputPatternElement, OutputPatternElementReference, Rule, Transformation}
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

object Apply{

    private def applyReferenceOnPattern[SME, SML, SMC, SMR, TME, TML](oper: OutputPatternElementReference[SME, SML, TME, TML],
                                                                      tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      sp: List[SME], iter: Int, te: TME)
    : Option[TML] =
        evalOutputPatternLinkExpr(sm, sp, te, iter, trace(tr, sm, mm), oper)


    private def applyElementOnPattern[SME, SML, SMC, SMR, TME, TML](ope: OutputPatternElement[SME, SML, TME, TML],
                                                                    tr: Transformation[SME, SML, SMC, TME, TML],
                                                                    sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                    sp: List[SME], iter: Int)
    : List[TML] =
        ope.getOutputElementReferences.flatMap(
            oper => evalOutputPatternElementExpr(sm, sp, iter, ope) match {
                case Some(l) => optionToList(applyReferenceOnPattern(oper, tr, sm, mm, sp, iter, l))
                case _ => List()
            }
        )


    private def applyIterationOnPattern[SME, SML, SMC, SMR, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                                      tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      sp: List[SME], iter: Int)
    : List[TML] =
        r.getOutputPatternElements.flatMap(o => applyElementOnPattern(o, tr, sm, mm, sp, iter))


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

}
