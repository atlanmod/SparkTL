package org.atlanmod.sequential.dynamic

import org.atlanmod.Util
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.tl.engine.sequential.TransformationEngineByRule
import org.atlanmod.transformation.dynamic.Class2Relational

object TestByRule {


    def main(args: Array[String]): Unit = {
        val model = Util.dynamic_simple_model(1, 1)
        print(model)
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.class2relational()
        val res = TransformationEngineByRule.execute(transformation, model, metamodel)
        println("----------------------------------")
        println("RESULT")
        println("----------------------------------")
        println(new DynamicModel(res.allModelElements, res.allModelLinks))
    }

}
