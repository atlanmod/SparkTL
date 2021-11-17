package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Model, OutputPatternElement, OutputPatternElementReference}

class OutputPatternElementImpl[SME, SML, SMC, TME, TML]
    (name: String,
     elementExpr: (Int, Model[SME, SML, SMC], List[SME]) => Option[TME],
     outputElemRefs: List[OutputPatternElementReference[SME, SML, SMC, TME, TML]] = List())
  extends OutputPatternElement[SME, SML, SMC, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getElementExpr: (Int, SM, List[SME]) => Option[TME] = elementExpr
    def getOutputElementReferences: List[OutputPatternElementReference[SME, SML, SMC, TME, TML]] = outputElemRefs

}
