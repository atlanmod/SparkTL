package org.atlanmod.tl.sequential

trait Transformation[SME, SML, SMC, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Types
    type SM = Model[SME, SML] // Source Model
    type TM = Model[SME, SML] // Source Model

    // Accessors
    def getRules: List[Rule[SME, SML, SMC, TME, TML]]
}
