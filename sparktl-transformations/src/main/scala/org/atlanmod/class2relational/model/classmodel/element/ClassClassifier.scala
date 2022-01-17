package org.atlanmod.class2relational.model.classmodel.element

import org.atlanmod.class2relational.model.classmodel.ClassElement

abstract class ClassClassifier(classname: String) extends ClassElement(classname: String) {
    def getName: String
}
