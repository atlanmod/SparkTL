package org.atlanmod.tl.sequential

trait OutputPatternElementReference[TL, SM, SME, TME, TML] {
    /*
     *  TL : TraceLink
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getLinkExpr: (List[TL], Int, SM, List[SME], TME) => Option[TML]
}
