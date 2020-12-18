package org.atlanmod.sequential.dynamic

import org.atlanmod.model.dynamic.{DynamicMetamodel, DynamicModel}
import org.atlanmod.model.generated.classModel
import org.eclipse.emf.ecore.resource.impl.ResourceImpl

object Test {
    private val classFactory = classModel.ClassFactory.eINSTANCE

    def create_simple_model(nclass: Int = 1, nattribute: Int = 1): DynamicModel = {
        val resource = new ResourceImpl()
        for (i <- 1 to nclass) {
            val a_class = classFactory.createClass()
            a_class.setId(i.toString)
            for (j <- 1 to nattribute) {
                val an_attribute = classFactory.createAttribute()
                an_attribute.setId(i.toString + "." + j.toString)
                a_class.getAttributes.add(an_attribute)
            }
            resource.getContents.add(a_class)
        }
        val a = new DynamicModel()
        a.load(resource)
        a
    }

    def main(args: Array[String]): Unit = {
        val model = create_simple_model()
        val metamodel = new DynamicMetamodel()
        val transformation = Class2Relational.transformation()
        val res = org.atlanmod.tl.engine.sequential.TransformationEngine.execute(transformation, model, metamodel)
        println(res)
    }
}