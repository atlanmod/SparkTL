package org.atlanmod.tl.model

trait OutputPatternElementReference[SME, SML, TME, TML]  extends Serializable  {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Type definitions
    type SM = Model[SME, SML]

    // Accessors
    def getLinkExpr: (TraceLinks[SME, TME], Int, SM, List[SME], TME) => Option[TML]
}
