package org.atlanmod.model.classmodel

import org.atlanmod.model.IdGenerator

class ClassDatatype extends ClassElement(ClassMetamodel.DATATYPE) {

    def this(name: String) {
        this()
        super.eSetProperty("id", IdGenerator.id())
        super.eSetProperty("name", name)
    }

    def this(id:String, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    override def getType: String = ClassMetamodel.DATATYPE
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def setName(name: String): Unit = super.eSetProperty("name", name)

    override def toString: String =
        super.getType + "([" + getId() + "] " + getName + ")"


}
