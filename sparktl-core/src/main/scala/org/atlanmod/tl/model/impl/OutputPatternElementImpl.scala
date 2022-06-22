package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Model, OutputPatternElement, OutputPatternElementReference}

class OutputPatternElementImpl[SME, SML, TME, TML, STL, TTL]
    (name: String,
     elementExpr: (Int, Model[SME, SML], List[SME]) => Option[TME],
     outputElemRefs: List[OutputPatternElementReference[SME, SML, TME, TML, STL, TTL]] = List())
  extends OutputPatternElement[SME, SML, TME, TML, STL, TTL] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getElementExpr: (Int, SM, List[SME]) => Option[TME] = elementExpr
    def getOutputElementReferences: List[OutputPatternElementReference[SME, SML, TME, TML, STL, TTL]] = outputElemRefs

}
