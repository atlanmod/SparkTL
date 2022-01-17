package org.atlanmod.class2relational.model.classmodel.link

import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass}
import org.atlanmod.class2relational.model.classmodel.ClassLink
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodelNaive

class ClassToAttributes (source: ClassClass, target: List[ClassAttribute])
  extends ClassLink(ClassMetamodelNaive.CLASS_ATTRIBUTES, source, target){

    def this(source: ClassClass, target: ClassAttribute) =
        this(source, List(target))

    override def getSource: ClassClass = source
    override def getTarget: List[ClassAttribute] = target

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.map(a => a.getId).mkString("[", ", ", "]") + ")"

}
