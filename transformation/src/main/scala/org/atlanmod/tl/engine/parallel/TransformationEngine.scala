package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.engine.{Apply, Instantiate}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}

import scala.reflect.ClassTag

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
                                                   sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                   sc: SparkContext)
    : Model[TME, TML] = {
        val tuples = sc.parallelize(allTuples(tr, sm))
        /* Instantiate */ val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))(ClassTag[SME])
        /* Apply */ val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))(ClassTag[SME])

        class tupleTModel(elements: RDD[TME], links: RDD[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements.collect.toList

            override def allModelLinks: List[TML] = links.collect.toList
        }

        new tupleTModel(elements, links)
    }

}
