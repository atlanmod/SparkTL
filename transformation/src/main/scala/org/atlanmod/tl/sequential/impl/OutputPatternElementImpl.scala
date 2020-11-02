package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.{OutputPatternElement, OutputPatternElementReference}

class OutputPatternElementImpl[TL, SM, SME, TME, TML](name: String, elementExpr: (Int, SM, List[SME]) => Option[TME],
                                                      outputElemRefs: List[OutputPatternElementReference[TL, SM, SME, TME, TML]])
  extends OutputPatternElement[TL, SM, SME, TME, TML] {
    /*
     *  TL : TraceLink
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getName: String = name
    def getElementExpr: (Int, SM, List[SME]) => Option[TME] = elementExpr
    def getOutputElementReferences: List[OutputPatternElementReference[TL, SM, SME, TME, TML]] = outputElemRefs

}
