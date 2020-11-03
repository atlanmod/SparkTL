package org.atlanmod.tl.sequential.spec

trait OutputPatternElement[SME, SML, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Type definition
    type SM = Model[SME, SML]

    // Accessors
    def getName: String

    def getElementExpr: (Int, SM, List[SME]) => Option[TME]

    def getOutputElementReferences: List[OutputPatternElementReference[SME, SML, TME, TML]]

}
