package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.spec.{Model, OutputPatternElement, OutputPatternElementReference}

class OutputPatternElementImpl[SME, SML, TME, TML]
    (name: String,
     elementExpr: (Int, Model[SME, SML], List[SME]) => Option[TME],
     outputElemRefs: List[OutputPatternElementReference[SME, SML, TME, TML]])
  extends OutputPatternElement[SME, SML, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getElementExpr: (Int, SM, List[SME]) => Option[TME] = elementExpr
    def getOutputElementReferences: List[OutputPatternElementReference[SME, SML, TME, TML]] = outputElemRefs

}
