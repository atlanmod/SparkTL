package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.{Rule, Transformation}

class TransformationImpl[SMC, SM, SME, TL, TME, TML](rules: List[Rule[SMC, SM, SME, TL, TME, TML]]) extends Transformation[SMC, SM, SME, TL, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TL : TraceLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getRules: List[Rule[SMC, SM, SME, TL, TME, TML]] = rules
}