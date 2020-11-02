package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.{Rule, Transformation}

class TransformationImpl[SME, SML, SMC, TME, TML](rules: List[Rule[SME, SML, SMC, TME, TML]])
  extends Transformation[SME, SML, SMC, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessor
    def getRules: List[Rule[SME, SML, SMC, TME, TML]] = rules
}