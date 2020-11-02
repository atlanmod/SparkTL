package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.OutputPatternElementReference

class OutputPatternElementReferenceImpl[TL, SM, SME, TME, TML](linkExpr: (List[TL], Int, SM, List[SME], TME) => Option[TML])
  extends OutputPatternElementReference[TL, SM, SME, TME, TML] {
    /*
     *  TL : TraceLink
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getLinkExpr: (List[TL], Int, SM, List[SME], TME) => Option[TML] = linkExpr

}
