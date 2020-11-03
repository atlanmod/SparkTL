package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.spec.{Model, OutputPatternElement, Rule}

class RuleImpl[SME, SML, SMC, TME, TML](name: String,
                                   types: List[SMC],
                                   from: (Model[SME, SML], List[SME]) => Option[Boolean],
                                   itExpr: (Model[SME, SML], List[SME]) => Option[Int],
                                   to: List[OutputPatternElement[SME, SML, TME, TML]])
  extends Rule[SME, SML, SMC, TME, TML]{
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getGuardExpr: (SM, List[SME]) => Option[Boolean] = from
    def getInTypes: List[SMC] = types
    def getIteratorExpr: (SM, List[SME]) => Option[Int] = itExpr
    def getOutputPatternElements: List[OutputPatternElement[SME, SML, TME, TML]] = to

    def findOutputPatternElemen(name: String): Option[OutputPatternElement[SME, SML, TME, TML]] =
        to.find(ope => ope.getName == name)
}
