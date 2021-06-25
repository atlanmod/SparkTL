package org.atlanmod.class2relational.model.classmodel

class ClassToAttributes (source: ClassClass, target: List[ClassAttribute])
  extends ClassLink(ClassMetamodel.CLASS_ATTRIBUTES, source, target){

    def this(source: ClassClass, target: ClassAttribute) =
        this(source, List(target))

    override def getSource: ClassClass = source
    override def getTarget: List[ClassAttribute] = target

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.map(a => a.getId).mkString("[", ", ", "]") + ")"

}
