package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.engine.{Apply, Instantiate}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineImpl extends ExperimentalTransformationEngine{
    override def execute[SME: ClassTag, SML, SMC, SMR, TME : ClassTag , TML : ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        npartition: Int, sc: SparkContext)
    : (Double, List[Double]) = {
        val t1_start = System.nanoTime()
        val tuples = sc.parallelize(allTuples(tr, sm), npartition)
        val t1_end = System.nanoTime()
        val t1 = (t1_end - t1_start) * 1000 / 1e9d

        val t2_start = System.nanoTime()
        val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t)).collect
        val t2_end = System.nanoTime()
        val t2 = (t2_end - t2_start) * 1000 / 1e9d

        val t3 = 0.0
        val t4 = 0.0

        val t5_start = System.nanoTime()
        /* Apply */ val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t)).collect
        val t5_end = System.nanoTime()
        val t5 = (t5_end - t5_start) * 1000 / 1e9d

        val time =  t1 + t2 + t3 + t4 + t5
        (time, List(t1,t2,t3,t4,t5))
    }
}
