package org.atlanmod.model.classmodel

abstract class ClassTypable(classname: String) extends ClassElement(classname: String) {
    def getName: String
}
