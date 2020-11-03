package org.atlanmod.tl.sequential

import org.atlanmod.tl.sequential.Eval.{evalGuardExpr, evalIteratorExpr, evalOutputPatternElementExpr}
import org.atlanmod.tl.sequential.spec.{Model, OutputPatternElement, Rule, Transformation}
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils

object Instantiate {

    def toModelClass[SME, SMC](sc: SMC, se: SME) : Option[Any] =
     Some(true)

    def checkTypes[SME, SMC](ses: List[SME], scs: List[SMC]): Boolean = {
        (ses, scs) match {
            case (se::ses2, sc::scs2) =>
                // TODO : add a metamodel instance, for toModelClass
                toModelClass(sc, se) match {
                    case Some(_) => checkTypes(ses2, scs2)
                    case _ => return false
                }
            case (List(), List()) => return true
            case _ => return false
        }
        true
    }

    def matchRuleOnPattern[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML], sp: List[SME]):
        Boolean =
        if (checkTypes(sp, r.getInTypes))
            evalGuardExpr(r, sm, sp) match {
                case Some(true) => true
                case _ => false
            }
        else false

    def matchPattern[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                              sp: List[SME]) : List[Rule[SME, SML, SMC, TME, TML]] =
        tr.getRules.filter(r => matchRuleOnPattern(r, sm, sp))

    def instantiateElementOnPattern[SME, SML, TME, TML](o: OutputPatternElement[SME, SML, TME, TML],
                                                        sm: Model[SME, SML], sp: List[SME], iter: Int): Option[TME] =
        evalOutputPatternElementExpr(sm, sp, iter, o)


    def instantiateIterationOnPattern[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                               sp: List[SME], iter: Int): List[TME] =
        r.getOutputPatternElements.flatMap(o => ListUtils.optionToList(instantiateElementOnPattern(o, sm, sp, iter)))

    def instantiateRuleOnPattern[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                          sp: List[SME]): List[TME] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(index => instantiateIterationOnPattern(r, sm, sp, index))

    def instantiatePattern[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                    sm: Model[SME, SML], sp: List[SME]): List[TME]={
        matchPattern(tr, sm, sp).flatMap(r => instantiateRuleOnPattern(r, sm, sp))
    }
}
