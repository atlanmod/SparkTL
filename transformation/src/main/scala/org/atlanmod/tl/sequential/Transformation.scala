package org.atlanmod.tl.sequential

trait Transformation[SMC, SM, SME, TL, TME, TML] {
    /*
     *  SMC : SourceModelClass
     *  SM : SourceModel
     *  SME : SourceModelElement
     *  TL : TraceLink
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     */

    // Accessors
    def getRules: List[Rule[SMC, SM, SME, TL, TME, TML]]
}
