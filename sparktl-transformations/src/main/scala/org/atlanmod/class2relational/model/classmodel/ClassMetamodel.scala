package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object ClassMetamodel {

    final val CLASS = "Class"
    final val ATTRIBUTE = "Attribute"
    final val DATATYPE = "Datatype"
    final val CLASS_ATTRIBUTES = "attributes"
    final val ATTRIBUTE_CLASS = "owner"
    final val ATTRIBUTE_TYPE = "type"

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink]
    = new DynamicMetamodel[DynamicElement, DynamicLink]("ClassMetamodel")

    private def getAttributeTypeOnLinks(attr: ClassAttribute, l: Iterator[ClassLink]): Option[ClassTypable] = {
        while(l.hasNext){
            val v = l.next()
            if(v.getSource.equals(attr) && v.isInstanceOf[AttributeToType])
                return Option(v.asInstanceOf[AttributeToType].getTargetType)
        }
        None
    }

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassTypable] = {
        getAttributeTypeOnLinks(attribute, model.allModelLinks)
    }

    private def getAttributeOwnerOnLinks(attr: ClassAttribute, l: Iterator[ClassLink]): Option[ClassClass] = {
        while(l.hasNext){
            val v = l.next()
            if(v.getSource.equals(attr) && v.isInstanceOf[AttributeToClass])
                return Some(v.asInstanceOf[AttributeToClass].getTargetClass)
        }
        None
    }

    def getAttributeOwner(attribute: ClassAttribute, model: ClassModel): Option[ClassClass] = {
        getAttributeOwnerOnLinks(attribute, model.allModelLinks)
    }

    def getClassAttributesOnLinks(cl: ClassClass, l: Iterator[ClassLink]): Option[List[ClassAttribute]] = {
        while(l.hasNext){
            val v = l.next()
            if(v.getSource.equals(cl) && v.isInstanceOf[ClassToAttributes])
                return Some(v.asInstanceOf[ClassToAttributes].getTarget)
        }
        None
    }

    def getClassAttributes(cl: ClassClass, model: ClassModel): Option[List[ClassAttribute]] = {
        getClassAttributesOnLinks(cl, model.allModelLinks)
    }

}
