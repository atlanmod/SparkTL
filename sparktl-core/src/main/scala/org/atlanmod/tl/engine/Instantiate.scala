package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Eval.{evalGuardExpr, evalIteratorExpr, evalOutputPatternElementExpr}
import org.atlanmod.tl.model.{Metamodel, Model, OutputPatternElement, Rule, Transformation}
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils

import scala.annotation.tailrec

object Instantiate {

    @tailrec
    def checkTypes[SME, SML, SMC, SMR](ses: List[SME], scs: List[SMC], mm: Metamodel[SME, SML, SMC, SMR])
    : Boolean =
        (ses, scs) match {
            case (se::ses2, sc::scs2) =>
                mm.toModelClass(sc, se) match {
                    case Some(_) => checkTypes(ses2, scs2, mm)
                    case _ => false
                }
            case (List(), List()) => true
            case (_,_) => false
        }


    def matchRuleOnPattern[SME, SML, SMC, SMR, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                                 sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                 sp: List[SME])
    : Boolean =
        if (checkTypes(sp, r.getInTypes, mm))
            evalGuardExpr(r, sm, sp) match {
                case Some(true) => true
                case _ => false
            }
        else false


    def matchPattern[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                   sm: Model[SME, SML],  mm: Metamodel[SME, SML, SMC, SMR],
                                                   sp: List[SME])
    : List[Rule[SME, SML, SMC, TME, TML]] =
        tr.getRules.filter(r => matchRuleOnPattern(r, sm, mm, sp))


    def instantiateElementOnPattern[SME, SML, SMC, TME, TML](o: OutputPatternElement[SME, SML, TME, TML],
                                                             sm: Model[SME, SML], sp: List[SME], iter: Int)
    : Option[TME] =
        evalOutputPatternElementExpr(sm, sp, iter, o)


    private def instantiateIterationOnPattern[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML],
                                                                       sm: Model[SME, SML], sp: List[SME], iter: Int)
    : List[TME] =
        r.getOutputPatternElements.flatMap(o => ListUtils.optionToList(instantiateElementOnPattern(o, sm, sp, iter)))


    private def instantiateRuleOnPattern[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                                  sp: List[SME])
    : List[TME] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(index => instantiateIterationOnPattern(r, sm, sp, index))


    def instantiatePattern[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                         sm: Model[SME, SML],  mm: Metamodel[SME, SML, SMC, SMR],
                                                         sp: List[SME])
    : List[TME]={
        val matched = matchPattern(tr, sm, mm, sp)
        matched.flatMap(r => instantiateRuleOnPattern(r, sm, sp))
    }
}
