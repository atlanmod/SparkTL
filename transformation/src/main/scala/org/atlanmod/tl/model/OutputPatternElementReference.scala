package org.atlanmod.tl.model

trait OutputPatternElementReference[SME, SML, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Type definitions
    type SM = Model[SME, SML]
    type TL = TraceLink[SME, TME]

    // Accessors
    def getLinkExpr: (List[TL], Int, SM, List[SME], TME) => Option[TML]
}
