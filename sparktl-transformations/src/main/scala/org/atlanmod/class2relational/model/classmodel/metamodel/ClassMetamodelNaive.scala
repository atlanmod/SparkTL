package org.atlanmod.class2relational.model.classmodel.metamodel

import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassClassifier}
import org.atlanmod.class2relational.model.classmodel.link.{AttributeToClass, AttributeToType, ClassToAttributes}
import org.atlanmod.class2relational.model.classmodel.{ClassLink, ClassModel}

object ClassMetamodelNaive extends ClassMetamodel {

    private def getAttributeTypeOnLinks(attr: ClassAttribute, l: Iterator[ClassLink]): Option[ClassClassifier] = {
        while (l.hasNext) {
            val v = l.next()
            if (v.getSource.equals(attr) && v.isInstanceOf[AttributeToType])
                return Option(v.asInstanceOf[AttributeToType].getTargetType)
        }
        None
    }

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassClassifier] = {
        getAttributeTypeOnLinks(attribute, model.allModelLinks.toIterator)
    }

    private def getAttributeOwnerOnLinks(attr: ClassAttribute, l: Iterator[ClassLink]): Option[ClassClass] = {
        while (l.hasNext) {
            val v = l.next()
            if (v.getSource.equals(attr) && v.isInstanceOf[AttributeToClass])
                return Some(v.asInstanceOf[AttributeToClass].getTargetClass)
        }
        None
    }

    def getAttributeOwner(attribute: ClassAttribute, model: ClassModel): Option[ClassClass] = {
        getAttributeOwnerOnLinks(attribute, model.allModelLinks.toIterator)
    }

    def getClassAttributesOnLinks(cl: ClassClass, l: Iterator[ClassLink]): Option[List[ClassAttribute]] = {
        while (l.hasNext) {
            val v = l.next()
            if (v.getSource.equals(cl) && v.isInstanceOf[ClassToAttributes])
                return Some(v.asInstanceOf[ClassToAttributes].getTarget)
        }
        None
    }

    def getClassAttributes(cl: ClassClass, model: ClassModel): Option[List[ClassAttribute]] = {
        getClassAttributesOnLinks(cl, model.allModelLinks.toIterator)
    }

}
