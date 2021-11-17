package org.atlanmod.tl.model

trait OutputPatternElementReference[SME, SML, SMC, TME, TML]  extends Serializable  {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Type definitions
    type SM = Model[SME, SML, SMC]

    // Accessors
    def getLinkExpr: (TraceLinks[SME, TME], Int, SM, List[SME], TME) => Option[TML]
}
