package org.atlanmod.parallel.generated

import org.apache.spark.SparkContext
import org.atlanmod.model.generated.classModel
import org.atlanmod.parallel.{EMFMetamodelSerializable, EMFModelSerializable}
import org.atlanmod.tl.util.SparkUtil
import org.eclipse.emf.ecore.resource.impl.ResourceImpl

object Test {

    private val classFactory = classModel.ClassFactory.eINSTANCE

    def create_simple_model(nclass: Int = 1, nattribute: Int = 1): EMFModelSerializable = {
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
        new EMFModelSerializable(resource)
    }

    def main(args: Array[String]): Unit = {
        val model = create_simple_model(1, 2)
        val metamodel = new EMFMetamodelSerializable
        val transformation = Class2Relational.transformation()
        val sc: SparkContext = SparkUtil.context

        val res = org.atlanmod.tl.engine.parallel.TransformationEngine.execute(transformation, model, metamodel, sc)

        println(res.allModelElements.size + " elemtns")
        println(res.allModelElements)
        println(res.allModelLinks.size + " links")
        println(res.allModelLinks)
    }

}
