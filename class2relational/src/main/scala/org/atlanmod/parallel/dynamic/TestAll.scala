package org.atlanmod.parallel.dynamic

import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.model.dynamic.classModel.{AttributeToClass, ClassAttribute, ClassClass, ClassElement, ClassLink, ClassModel, ClassToAttributes}
import org.atlanmod.tl.util.SparkUtil
import org.atlanmod.transformation.dynamic.Class2Relational

object TestAll {

    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1): ClassModel = {
        var elements : List[ClassElement] = List()
        var links : List[ClassLink] = List()
        for(i <- 1 to nclass){
            val cc = new ClassClass(i.toString, "")
            elements = cc :: elements
            var cc_attributes : List[ClassAttribute] = List()
            for (j <- 1 to nattribute) {
                val ca = new ClassAttribute(i.toString + "." + j.toString, "")
                elements = ca :: elements
                links = new AttributeToClass(ca, cc) :: links
                cc_attributes = ca :: cc_attributes
            }
            links = new ClassToAttributes(cc, cc_attributes) :: links

        }
        new ClassModel(elements, links)
    }

    def main(args: Array[String]): Unit = {
        val model = TestAll.dynamic_simple_model(1, 2)
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.transformation()
        val sc = SparkUtil.context
        val res = org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(transformation, model, metamodel, sc)
        val res_byrule = org.atlanmod.tl.engine.parallel.TransformationEngineByRule.execute(transformation, model, metamodel, sc)
        val res_twophase = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation, model, metamodel, sc)
    }
}
