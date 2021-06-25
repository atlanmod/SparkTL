package org.atlanmod.class2relational.model.classmodel

abstract class ClassTypable(classname: String) extends ClassElement(classname: String) {
    def getName: String
}
