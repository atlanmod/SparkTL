package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object ClassMetamodelWithMap {

    final val CLASS = "Class"
    final val ATTRIBUTE = "Attribute"
    final val DATATYPE = "Datatype"
    final val CLASS_ATTRIBUTES = "attributes"
    final val ATTRIBUTE_CLASS = "owner"
    final val ATTRIBUTE_TYPE = "type"

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink]
    = new DynamicMetamodel[DynamicElement, DynamicLink]("ClassMetamodel")

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassTypable] = {
        metamodel.allLinksOfTypeOfElement(attribute, ATTRIBUTE_TYPE, model) match {
            case Some(e: List[ClassTypable]) => e.headOption
            case _ => None
        }
    }

    def getAttributeOwner(attribute: ClassAttribute, model: ClassModel): Option[ClassClass] = {
        metamodel.allLinksOfTypeOfElement(attribute, ATTRIBUTE_CLASS, model) match {
            case Some(e: List[ClassClass]) => e.headOption
            case _ => None
        }
    }

    def getClassAttributes(cl: ClassClass, model: ClassModel): Option[List[ClassAttribute]] = {
        metamodel.allLinksOfTypeOfElement(cl, CLASS_ATTRIBUTES, model) match {
            case Some(e: List[ClassAttribute]) => Some(e)
            case _ => None
        }
    }

}
