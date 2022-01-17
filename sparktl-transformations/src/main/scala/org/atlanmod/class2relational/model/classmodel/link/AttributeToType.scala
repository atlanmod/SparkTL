package org.atlanmod.class2relational.model.classmodel.link

import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClassifier}
import org.atlanmod.class2relational.model.classmodel.ClassLink
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodelNaive

class AttributeToType(source: ClassAttribute, target: ClassClassifier)
  extends ClassLink(ClassMetamodelNaive.ATTRIBUTE_TYPE, source, List(target)) {

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.getId + ")"

    override def getSource: ClassAttribute = source

    override def getTarget: List[ClassClassifier] = List(target)

    def getTargetType: ClassClassifier = target

}
