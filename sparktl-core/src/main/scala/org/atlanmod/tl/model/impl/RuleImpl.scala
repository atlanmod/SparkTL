package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Model, OutputPatternElement, Rule}

object Util {
    def default_from[SME, SML, SMC]: (Model[SME, SML, SMC], List[SME]) => Option[Boolean] = (_, _) => Some(true)
    def default_itExpr[SME, SML, SMC]: (Model[SME, SML, SMC], List[SME]) => Option[Int] = (_, _) => Some(1)
}

class RuleImpl[SME, SML, SMC, TME, TML](name: String,
                                   types: List[SMC],
                                   from: (Model[SME, SML, SMC], List[SME]) => Option[Boolean],
                                   itExpr: (Model[SME, SML, SMC], List[SME]) => Option[Int],
                                   to: List[OutputPatternElement[SME, SML, SMC, TME, TML]])
  extends Rule[SME, SML, SMC, TME, TML]{
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    def this(name: String, types: List[SMC], to: List[OutputPatternElement[SME, SML, SMC, TME, TML]]){
        this(name, types, Util.default_from[SME, SML, SMC], Util.default_itExpr[SME, SML, SMC], to)
    }

    def this(name: String, types: List[SMC], from: (Model[SME, SML, SMC], List[SME]) => Option[Boolean],
             to: List[OutputPatternElement[SME, SML, SMC, TME, TML]]){
        this(name, types, from, Util.default_itExpr[SME, SML, SMC], to)
    }

    // Accessors
    def getName: String = name
    def getGuardExpr: (SM, List[SME]) => Option[Boolean] = from
    def getInTypes: List[SMC] = types
    def getIteratorExpr: (SM, List[SME]) => Option[Int] = itExpr
    def getOutputPatternElements: List[OutputPatternElement[SME, SML, SMC, TME, TML]] = to

    override def findOutputPatternElement(name: String): Option[OutputPatternElement[SME, SML, SMC, TME, TML]] =
        to.find(ope => ope.getName.equals(name))

}
