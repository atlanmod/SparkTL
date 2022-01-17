package org.atlanmod.class2relational.model.classmodel.link

import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass}
import org.atlanmod.class2relational.model.classmodel.ClassLink
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodelNaive

class AttributeToClass(source: ClassAttribute, target: ClassClass)
  extends ClassLink(ClassMetamodelNaive.ATTRIBUTE_CLASS, source, List(target)) {

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + getTargetClass.getId + ")"

    override def getSource: ClassAttribute = source

    override def getTarget: List[ClassClass] = List(target)

    def getTargetClass: ClassClass = target

}
