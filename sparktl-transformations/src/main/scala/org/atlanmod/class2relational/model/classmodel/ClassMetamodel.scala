package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

import scala.annotation.tailrec

object ClassMetamodel {

    final val CLASS = "Class"
    final val ATTRIBUTE = "Attribute"
    final val DATATYPE = "Datatype"
    final val CLASS_ATTRIBUTES = "attributes"
    final val ATTRIBUTE_CLASS = "owner"
    final val ATTRIBUTE_TYPE = "type"

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink]
    = new DynamicMetamodel[DynamicElement, DynamicLink]()

    @tailrec
    private def getAttributeTypeOnLinks(attr: ClassAttribute, l: List[ClassLink]): Option[ClassTypable] = {
        l match {
            case (h: AttributeToType) :: l2 =>
                if (h.getSource.equals(attr)){
                    Some(h.getTargetType)
                } else {
                    getAttributeTypeOnLinks(attr, l2)
                }
            case _ :: l2 => getAttributeTypeOnLinks(attr, l2)
            case List() => None
        }
    }

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassTypable] = {
        getAttributeTypeOnLinks(attribute, model.allModelLinks)
    }

    @tailrec
    private def getAttributeOwnerOnLinks(attr: ClassAttribute, l: List[ClassLink]): Option[ClassClass] = {
        l match {
            case (h: AttributeToClass) :: l2 =>
                if (h.getSource.equals(attr)){
                    Some(h.getTargetClass)
                } else {
                    getAttributeOwnerOnLinks(attr, l2)
                }
            case _ :: l2 => getAttributeOwnerOnLinks(attr, l2)
            case List() => None
        }
    }

    def getAttributeOwner(attribute: ClassAttribute, model: ClassModel): Option[ClassClass] = {
        getAttributeOwnerOnLinks(attribute, model.allModelLinks)
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

//    def getIdTypeonElements(elements: List[ClassElement]): Option[ClassDatatype] = {
//        elements match {
//            case (h: ClassDatatype) :: l2 if h.isId => Some(h)
//            case _ :: l2 => getIdTypeonElements(l2)
//            case List() => None
//        }
//    }
//
//    def getIdType(model: ClassModel): Option[ClassDatatype] = {
//        getIdTypeonElements(model.allModelElements)
//    }

}
