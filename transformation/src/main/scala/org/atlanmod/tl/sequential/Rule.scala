package org.atlanmod.tl.sequential

trait Rule[SMC, SM, SME, TL, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TL : TraceLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String
    def getGuardExpr: (SM, List[SME]) => Option[Boolean]
    def getInTypes: List[SMC]
    def getIteratorExpr: (SM, List[SME]) => Option[Int]
    def getOutputPatternElements: List[OutputPatternElement[TL, SM, SME, TME, TML]]

    def findOutputPatternElemen(name: String): Option[OutputPatternElement[TL, SM, SME, TME, TML]]

}
