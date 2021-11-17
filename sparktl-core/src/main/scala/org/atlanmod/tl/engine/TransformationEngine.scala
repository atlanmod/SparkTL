package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

trait TransformationEngine {

    def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag, TMC: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext,
     makeModel: (Iterable[TME], Iterable[TML]) => Model[TME, TML, TMC] = (a, b) => ModelUtil.makeTupleModel[TME, TML, TMC](a, b))
    : Model[TME, TML, TMC]
}
