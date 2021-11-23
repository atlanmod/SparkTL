package org.atlanmod.tl.model

trait OutputPatternElement[SME, SML, TME, TML] extends Serializable {
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
