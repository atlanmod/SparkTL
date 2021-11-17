package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Model, OutputPatternElementReference, TraceLinks}

class OutputPatternElementReferenceImpl[SME, SML, SMC, TME, TML]
    (linkExpr: (TraceLinks[SME, TME], Int, Model[SME, SML, SMC], List[SME], TME) => Option[TML])
  extends OutputPatternElementReference[SME, SML, SMC, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessor
    def getLinkExpr: (TraceLinks[SME, TME], Int, Model[SME, SML, SMC], List[SME], TME) => Option[TML] = linkExpr

}