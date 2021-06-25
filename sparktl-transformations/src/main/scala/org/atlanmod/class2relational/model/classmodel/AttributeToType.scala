package org.atlanmod.class2relational.model.classmodel

class AttributeToType(source: ClassAttribute, target: ClassTypable)
  extends ClassLink(ClassMetamodel.ATTRIBUTE_TYPE, source, List(target)) {

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.getId + ")"

    override def getSource: ClassAttribute = source

    override def getTarget: List[ClassTypable] = List(target)

    def getTargetType: ClassTypable = target

}
