package org.atlanmod.tl.sequential

import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.sequential.Utils.allTuples

class Engine[SME, SML, SMC, SMR, TME, TML, TMC] {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */

    type SourceModelType = Model[SME, SML]
    type SourceMetamodelType = Metamodel[SME, SML, SMC, SMR]
    type TargetModelType = Model[TME, TML]

    def execute(tr: Transformation[SME, SML, SMC, TME, TML],
                sm: SourceModelType, mm: SourceMetamodelType)
    : TargetModelType = {
        val tuples = allTuples(tr, sm)
        /* Instantiate */ val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        /* Apply */       val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))

        class tupleTModel(elements: List[TME], links: List[TML]) extends TargetModelType {
            override def allModelElements: List[TME] = elements
            override def allModelLinks: List[TML] = links
        }

        new tupleTModel(elements, links)
    }

}