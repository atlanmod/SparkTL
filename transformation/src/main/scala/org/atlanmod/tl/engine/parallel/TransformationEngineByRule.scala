package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.engine.{Apply, Instantiate, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import spire.ClassTag

object TransformationEngineByRule extends TransformationEngine {

    override def execute[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           sc: SparkContext = null)
    : Model[TME, TML] = {
        val tuples : RDD[List[SME]] = sc.parallelize(allTuplesByRule(tr, sm, mm))
        /* Instantiate */ val elements : RDD[TME] = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        /* Apply */ val links : RDD[TML] = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))

        class tupleTModel(elements: RDD[TME], links: RDD[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements.collect.toList
            override def allModelLinks: List[TML] = links.collect.toList
        }

        new tupleTModel(elements, links)
    }
}
