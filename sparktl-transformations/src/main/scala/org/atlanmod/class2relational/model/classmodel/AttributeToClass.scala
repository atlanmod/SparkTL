package org.atlanmod.class2relational.model.classmodel

class AttributeToClass(source: ClassAttribute, target: ClassClass)
  extends ClassLink(ClassMetamodel.ATTRIBUTE_CLASS, source, List(target)) {

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + getTargetClass.getId + ")"

    override def getSource: ClassAttribute = source

    override def getTarget: List[ClassClass] = List(target)

    def getTargetClass: ClassClass = target

}
