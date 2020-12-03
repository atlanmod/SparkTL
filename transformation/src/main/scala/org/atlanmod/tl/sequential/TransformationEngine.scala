package org.atlanmod.tl.sequential

import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.sequential.Utils.allTuples

object TransformationEngine {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */

    def execute[SME, SML, SMC, SMR, TME, TML, TMC](tr: Transformation[SME, SML, SMC, TME, TML],
                                                   sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    :Model[TME, TML] = {
        val tuples = allTuples(tr, sm)
        /* Instantiate */ val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        /* Apply */       val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))

        class tupleTModel(elements: List[TME], links: List[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements
            override def allModelLinks: List[TML] = links
        }

        new tupleTModel(elements, links)
    }

}