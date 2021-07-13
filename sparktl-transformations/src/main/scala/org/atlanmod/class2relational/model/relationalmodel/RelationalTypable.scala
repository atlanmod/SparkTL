package org.atlanmod.class2relational.model.relationalmodel

abstract class RelationalTypable(classname: String) extends RelationalElement(classname) {
    def getName : String
}
