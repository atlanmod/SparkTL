package org.atlanmod

import org.atlanmod.tl.sequential.TransformationEngine
import org.eclipse.emf.ecore.resource.impl.ResourceImpl

object Main {

    private val classFactory =  classModel.ClassFactory.eINSTANCE

    def model(nclass : Int = 1, nattribute : Int = 1) : EMFModel = {
        val resource = new ResourceImpl()
        for (i <- 1 to nclass) {
            val a_class = classFactory.createClass()
            a_class.setId(i.toString)
            for (j <- 1 to nattribute) {
                val an_attribute = classFactory.createAttribute()
                an_attribute.setId(i.toString+"."+j.toString)
                a_class.getAttributes.add(an_attribute)
            }
            resource.getContents.add(a_class)
        }
        new EMFModel(resource)
    }

    def main (args : Array[String]): Unit = {
        val m = model(1, 2)
        val mm = new EMFMetamodel
        val tr = Class2Relational.transformation()
        val res =  TransformationEngine.execute(tr, m, mm)
        println(res.allModelElements.size + " elemtns")
        println(res.allModelElements)
        println(res.allModelLinks.size + " links")
        println(res.allModelLinks)


    }
}
