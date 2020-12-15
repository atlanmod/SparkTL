package org.atlanmod.sequential

import org.apache.spark.SparkContext
import org.atlanmod.classModel
import org.atlanmod.parallel.{EMFMetamodelSerializable, EMFModelSerializable}
import org.atlanmod.tl.util.SparkUtil
import org.eclipse.emf.ecore.resource.impl.ResourceImpl

object Test {

    private val classFactory = classModel.ClassFactory.eINSTANCE

    def model(nclass: Int = 1, nattribute: Int = 1): (EMFModel, EMFModelSerializable) = {
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
        (new EMFModel(resource), new EMFModelSerializable(resource))
    }

    def main(args: Array[String]): Unit = {
        val models = model(1, 2)
//        val m = model(1, 2)
        val metamodels = (new EMFMetamodel, new EMFMetamodelSerializable)
        val transformations = (
          org.atlanmod.sequential.Class2Relational.transformation(),
          org.atlanmod.parallel.Class2Relational.transformation()
        )
        val sc: SparkContext = SparkUtil.context
        val res_seq = org.atlanmod.tl.engine.sequential.TransformationEngine.execute(transformations._1, models._1, metamodels._1)

        println(res_seq.allModelElements.size + " elemtns")
        println(res_seq.allModelElements)
        println(res_seq.allModelLinks.size + " links")
        println(res_seq.allModelLinks)

//        val res_par = org.atlanmod.tl.engine.parallel.TransformationEngine.execute(transformations._2, models._2, metamodels._2, sc)
//
//        println(res_par.allModelElements.size + " elemtns")
//        println(res_par.allModelElements)
//        println(res_par.allModelLinks.size + " links")
//        println(res_par.allModelLinks)
    }

}
