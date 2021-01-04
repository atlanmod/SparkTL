package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.{Apply, Instantiate}
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineImpl extends ExperimentalTransformationEngine{
    override def execute[SME, SML, SMC, SMR, TME : ClassTag , TML : ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                              sc: SparkContext)
    : (Double, List[Double]) = {
        val t1 = System.nanoTime
        val tuples : RDD[List[SME]] = sc.parallelize(allTuples(tr, sm))
        val t2 = System.nanoTime
        /* Instantiate */ val elements : RDD[TME] = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        val t3 = System.nanoTime
        /* Apply */ val links : RDD[TML] = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))
        val t4 = System.nanoTime

        val t1_to_t2 = (t2 - t1) * 1000 / 1e9d
        val t2_to_t3 = (t3 - t2) * 1000 / 1e9d
        val t3_to_t4 = (t4 - t3) * 1000 / 1e9d
        val t1_to_t4 = (t4 - t1) * 1000 / 1e9d

        (t1_to_t4, List(t1_to_t2, t2_to_t3, t3_to_t4))
    }
}
