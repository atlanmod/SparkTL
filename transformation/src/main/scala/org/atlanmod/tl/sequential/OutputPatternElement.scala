package org.atlanmod.tl.sequential

trait OutputPatternElement[TL, SM, SME, TME, TML] {
    /*
     *  TL : TraceLink
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String
    def getElementExpr: (Int, SM, List[SME]) => Option[TME]
    def getOutputElementReferences: List[OutputPatternElementReference[TL, SM, SME, TME, TML]]

}
