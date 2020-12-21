package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import spire.ClassTag

trait TransformationEngine {

    def execute[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                  sc: SparkContext)
    : Model[TME, TML]
}
