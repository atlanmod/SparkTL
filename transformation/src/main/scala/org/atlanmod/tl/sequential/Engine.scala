package org.atlanmod.tl.sequential

import org.atlanmod.tl.sequential.spec.{Model, Transformation}
import org.atlanmod.tl.util.TupleUtils

class Engine[SME, SML, SMC, TME, TML, TMC] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */

    type SourceModelType = Model[SME, SML]
    type TargetModelType = Model[TME, TML]

    private def maxArity(tr: Transformation[SME, SML, SMC, TME, TML]): Int =
        tr.getRules.map(r => r.getInTypes).map(l => l.length).max

    private def allModelElements(sm: SourceModelType): List[SME] =
        sm.allModelElements

    private def allTuples(tr: Transformation[SME, SML, SMC, TME, TML], sm: SourceModelType)
    : List[List[SME]] =
        TupleUtils.tuples_up_to_n (allModelElements(sm), maxArity(tr))

//    def execute(tr: Transformation[SME, SML, SMC, TME, TML], sm: SourceModelType): TargetModelType = {
//        val tuples = allTuples(tr, sm)
//        val elements = tuples.flatMap(Instantiate.instantiatePattern(tr, sm))
//        val links = tuples.flatMap(Apply.applyPattern(tr, sm))
//    }

}