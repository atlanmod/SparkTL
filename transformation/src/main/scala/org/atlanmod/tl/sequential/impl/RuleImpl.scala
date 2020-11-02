package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.{OutputPatternElement, Rule}

class RuleImpl[SMC, SM, SME, TL, TME, TML](name: String,
                                           types: List[SMC],
                                           from: (SM, List[SME]) => Option[Boolean],
                                           itExpr: (SM, List[SME]) => Option[Int],
                                           to: List[OutputPatternElement[TL, SM, SME, TME, TML]])
  extends Rule[SMC, SM, SME, TL, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TL : TraceLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getGuardExpr: (SM, List[SME]) => Option[Boolean] = from
    def getInTypes: List[SMC] = types
    def getIteratorExpr: (SM, List[SME]) => Option[Int] = itExpr
    def getOutputPatternElements: List[OutputPatternElement[TL, SM, SME, TME, TML]] = to

    def findOutputPatternElemen(name: String): Option[OutputPatternElement[TL, SM, SME, TME, TML]] =
        to.find(ope => ope.getName == name)
}
