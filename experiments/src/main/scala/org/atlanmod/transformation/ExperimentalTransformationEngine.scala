package org.atlanmod.transformation

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}

import scala.reflect.ClassTag

trait ExperimentalTransformationEngine {
    def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],npartition: Int,
      sc: SparkContext = null) : (Double, List[Double])
}
