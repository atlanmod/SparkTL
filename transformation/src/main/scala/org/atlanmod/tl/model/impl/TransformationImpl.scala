package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{Rule, Transformation}

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