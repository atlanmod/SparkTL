package org.atlanmod.model.classmodel

class AttributeToDatatype(source: ClassAttribute, target: ClassDatatype)
  extends ClassLink(ClassMetamodel.ATTRIBUTE_TYPE, source, List(target)) {

    override def toString: String =
        "(" + source.getId() + ", " + super.getType + ", " + target.getId() + ")"

    override def getSource: ClassAttribute = source

    def getTargetDatatype: ClassDatatype = target

    override def getTarget: List[ClassDatatype] = List(target)

    def getTargetClass: ClassDatatype = target

}
