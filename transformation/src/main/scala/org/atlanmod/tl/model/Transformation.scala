package org.atlanmod.tl.model

trait Transformation[SME, SML, SMC, TME, TML] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Types
    type SM = Model[SME, SML] // Source Model
    type TM = Model[SME, SML] // Source Model

    // Accessors
    def getRules: List[Rule[SME, SML, SMC, TME, TML]]
}
