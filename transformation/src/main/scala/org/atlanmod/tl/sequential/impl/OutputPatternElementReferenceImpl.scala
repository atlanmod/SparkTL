package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.{Model, OutputPatternElementReference, TraceLink}

class OutputPatternElementReferenceImpl[SME, SML, TME, TML]
    (linkExpr: (List[TraceLink[SME, TME]], Int, Model[SME, SML], List[SME], TME) => Option[TML])
  extends OutputPatternElementReference[SME, SML, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessor
    def getLinkExpr: (List[TL], Int, SM, List[SME], TME) => Option[TML] = linkExpr

}