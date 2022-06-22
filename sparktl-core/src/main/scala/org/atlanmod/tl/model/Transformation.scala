package org.atlanmod.tl.model

trait Transformation[SME, SML, SMC, TME, TML, STL, TTL] extends Serializable {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  STL : SourceTraceLink
     *  TTL : TargetTraceLink
     */

    // Types
//    type SM = Model[SME, SML, SMC] // Source Model
//    type TM = Model[TME, TML, TMC] // Target Model

    // Accessors
    def getRules: List[Rule[SME, SML, SMC, TME, TML, STL, TTL]]
}
