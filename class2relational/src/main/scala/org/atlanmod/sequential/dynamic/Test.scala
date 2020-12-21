package org.atlanmod.sequential.dynamic

import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel, DynamicModel}
import org.atlanmod.model.dynamic.classModel._
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
        val model = dynamic_simple_model(1, 2)
        print(model)
        val metamodel = new DynamicMetamodel[DynamicElement, DynamicLink]()
        val transformation = Class2Relational.transformation()
        val res = org.atlanmod.tl.engine.sequential.TransformationEngine.execute(transformation, model, metamodel)
        println("----------------------------------")
        println("RESULT")
        println("----------------------------------")
        println(new DynamicModel(res.allModelElements, res.allModelLinks))
    }

}