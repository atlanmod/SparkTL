package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

trait TransformationEngine {

    def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML]
}
