package org.atlanmod.sequential.dynamic

import org.atlanmod.Util
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.tl.engine.sequential.TransformationEngineByRule
import org.atlanmod.transformation.dynamic.Class2Relational

object TestByRuleMV {


    def main(args: Array[String]): Unit = {
        val model = Util.dynamic_simple_model(10, 5)
        print(model)
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.class2relationalMV()
        val res = TransformationEngineByRule.execute(transformation, model, metamodel)
        println("----------------------------------")
        println("RESULT")
        println("----------------------------------")
        println(new DynamicModel(res.allModelElements, res.allModelLinks))
    }

}