package org.atlanmod.tl.engine.sequential

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.engine.{Apply, Instantiate, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import spire.ClassTag

object TransformationEngineImpl extends TransformationEngine {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */

    override def execute[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           sc: SparkContext = null)
    : Model[TME, TML] = {
        val tuples = allTuples(tr, sm)
        /* Instantiate */ val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        /* Apply */ val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))

        class tupleTModel(elements: List[TME], links: List[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements
            override def allModelLinks: List[TML] = links
        }

        new tupleTModel(elements, links)
    }
}