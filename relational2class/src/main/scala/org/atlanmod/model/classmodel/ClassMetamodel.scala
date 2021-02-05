package org.atlanmod.model.classmodel

import scala.annotation.tailrec

object ClassMetamodel {

    final val CLASS = "Class"
    final val ATTRIBUTE = "Attribute"
    final val DATATYPE = "Datatype"
    final val CLASS_ATTRIBUTES = "attributes"
    final val ATTRIBUTE_CLASS = "owner"
    final val ATTRIBUTE_TYPE = "type"

    @tailrec
    private def getAttributeDatatypeOnLinks(attr: ClassAttribute, l: List[ClassLink]): Option[ClassDatatype] = {
        l match {
            case (h: AttributeToDatatype) :: l2 =>
                if (h.getSource.equals(attr)){
                    Some(h.getTargetClass)
                } else {
                    getAttributeDatatypeOnLinks(attr, l2)
                }
            case _ :: l2 => getAttributeDatatypeOnLinks(attr, l2)
            case List() => None
        }
    }

    def getAttributeDatatype(attribute: ClassAttribute, model: ClassModel): Option[ClassDatatype] = {
        getAttributeDatatypeOnLinks(attribute, model.allModelLinks)
    }

    @tailrec
    private def getAttributeTypeOnLinks(attr: ClassAttribute, l: List[ClassLink]): Option[ClassClass] = {
        l match {
            case (h: AttributeToClass) :: l2 =>
                if (h.getSource.equals(attr)){
                    Some(h.getTargetClass)
                } else {
                    getAttributeTypeOnLinks(attr, l2)
                }
            case _ :: l2 => getAttributeTypeOnLinks(attr, l2)
            case List() => None
        }
    }

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassClass] = {
        getAttributeTypeOnLinks(attribute, model.allModelLinks)
    }

    @tailrec
    def getClassAttributesOnLinks(cl: ClassClass, l: List[ClassLink]): Option[List[ClassAttribute]] = {
        l match {
            case (h: ClassToAttributes) :: l2 =>
                if (h.getSource.equals(cl)) {
                    Some(h.getTarget)
                } else {
                    getClassAttributesOnLinks(cl, l2)
                }
            case _ :: l2 => getClassAttributesOnLinks(cl, l2)
            case List() => None
        }
    }

    def getClassAttributes(cl: ClassClass, model: ClassModel): Option[List[ClassAttribute]] = {
        getClassAttributesOnLinks(cl, model.allModelLinks)
    }
}
