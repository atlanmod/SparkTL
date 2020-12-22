package org.atlanmod.parallel.dynamic

import org.atlanmod.Util
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.tl.util.SparkUtil
import org.atlanmod.transformation.dynamic.Class2Relational

object TestTwoPhase {

    def main(args: Array[String]): Unit = {
        val model = Util.dynamic_simple_model(1, 2)
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.transformation()
        val sc = SparkUtil.context
        val res = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation, model, metamodel, sc)
        println("----------------------------------")
        println("RESULT")
        println("----------------------------------")
        println(new DynamicModel(res.allModelElements, res.allModelLinks))
    }

}
