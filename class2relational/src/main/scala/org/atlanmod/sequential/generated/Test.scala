package org.atlanmod.sequential.generated

import org.atlanmod.model.generated.classModel
import org.atlanmod.sequential.{EMFMetamodel, EMFModel}
import org.eclipse.emf.ecore.resource.impl.ResourceImpl

object Test {

    private val classFactory = classModel.ClassFactory.eINSTANCE

    def create_simple_model(nclass: Int = 1, nattribute: Int = 1): EMFModel = {
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
        new EMFModel(resource)
    }

    def main(args: Array[String]): Unit = {
        val m: EMFModel = create_simple_model(1, 2)
        val metamodel = new EMFMetamodel
        val transformation = Class2Relational.transformation()
        val res_seq = org.atlanmod.tl.engine.sequential.TransformationEngine.execute(transformation, m, metamodel)

        println(res_seq.allModelElements.size + " elemtns")
        println(res_seq.allModelElements)
        println(res_seq.allModelLinks.size + " links")
        println(res_seq.allModelLinks)
    }

}
