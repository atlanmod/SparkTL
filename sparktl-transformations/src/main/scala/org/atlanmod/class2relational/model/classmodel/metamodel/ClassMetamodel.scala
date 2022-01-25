package org.atlanmod.class2relational.model.classmodel.metamodel

import org.atlanmod.class2relational.model.classmodel.ClassModel
import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassClassifier}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait ClassMetamodel extends Serializable {

    final val CLASS = "Class"
    final val ATTRIBUTE = "Attribute"
    final val DATATYPE = "Datatype"
    final val CLASS_ATTRIBUTES = "attributes"
    final val ATTRIBUTE_CLASS = "owner"
    final val ATTRIBUTE_TYPE = "type"

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("ClassMetamodel")

    def getAttributeType(attribute: ClassAttribute, model: ClassModel): Option[ClassClassifier]

    def getAttributeOwner(attribute: ClassAttribute, model: ClassModel): Option[ClassClass]

    def getClassAttributes(cl: ClassClass, model: ClassModel): Option[List[ClassAttribute]]
}
