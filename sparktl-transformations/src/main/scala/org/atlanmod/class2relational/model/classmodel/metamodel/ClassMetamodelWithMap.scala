package org.atlanmod.class2relational.model.classmodel.metamodel

import org.atlanmod.class2relational.model.classmodel.ClassModel
import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassClassifier}

object ClassMetamodelWithMap extends ClassMetamodel  {

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassClassifier] = {
        metamodel.allLinksOfTypeOfElement(attribute, ATTRIBUTE_TYPE, model) match {
            case Some(e: List[ClassClassifier]) => e.headOption
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
