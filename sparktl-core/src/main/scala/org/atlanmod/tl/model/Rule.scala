package org.atlanmod.tl.model

trait Rule[SME, SML, SMC, TME, TML]  extends Serializable {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Type definitions
    type SM = Model[SME, SML]
    type TL = TraceLink[SME, TME]

    // Accessors
    def getName: String

    def getGuardExpr: (SM, List[SME]) => Option[Boolean]

    def getInTypes: List[SMC]

    def getIteratorExpr: (SM, List[SME]) => Option[Int]

    def getOutputPatternElements: List[OutputPatternElement[SME, SML, TME, TML]]

    def findOutputPatternElement(name: String): Option[OutputPatternElement[SME, SML, TME, TML]]

}
