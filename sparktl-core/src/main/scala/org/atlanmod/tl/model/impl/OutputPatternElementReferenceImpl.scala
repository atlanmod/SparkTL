package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Model, OutputPatternElementReference, TraceLinks}

class OutputPatternElementReferenceImpl[SME, SML, TME, TML, STL, TTL]
    (linkExpr: (TraceLinks[STL, TTL], Int, Model[SME, SML], List[SME], TME) => Option[TML])
  extends OutputPatternElementReference[SME, SML, TME, TML, STL, TTL] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessor
    def getLinkExpr: (TraceLinks[STL, TTL], Int, Model[SME, SML], List[SME], TME) => Option[TML] = linkExpr

}