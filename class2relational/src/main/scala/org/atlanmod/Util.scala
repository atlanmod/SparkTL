package org.atlanmod

import org.atlanmod.model.dynamic.classModel.{ClassClass, ClassElement, ClassLink, ClassModel, ClassAttribute,
    AttributeToClass, ClassToAttributes}

object Util {
    def dynamic_simple_model(nclass: Int = 1, nattribute: Int = 1): ClassModel = {
        var elements : List[ClassElement] = List()
        var links : List[ClassLink] = List()
        for(i <- 1 to nclass){
            val cc = new ClassClass(i.toString, "name"+i.toString)
            elements = cc :: elements
            var cc_attributes : List[ClassAttribute] = List()
            for (j <- 1 to nattribute) {
                val ca = new ClassAttribute(i.toString + "." + j.toString, "name"+ i.toString + "." + j.toString,
                    true)
                ca.setClass_(cc)
                elements = ca :: elements
                links = new AttributeToClass(ca, cc) :: links
                cc_attributes = ca :: cc_attributes
            }
            if (cc_attributes.nonEmpty){
                cc.addAttributes(cc_attributes)
                links = new ClassToAttributes(cc, cc_attributes) :: links
            }
        }
        new ClassModel(elements, links)
    }
}
