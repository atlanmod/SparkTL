package org.atlanmod.class2relational.model.relationalmodel.element

import org.atlanmod.class2relational.model.relationalmodel.RelationalElement

abstract class RelationalClassifier(classname: String) extends RelationalElement(classname) {
    def getName : String
}
