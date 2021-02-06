package org.atlanmod.model.classmodel

import org.atlanmod.model.IdGenerator

class ClassDatatype extends ClassElement(ClassMetamodel.DATATYPE) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation")
    def this(name: String) {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    def this(id:String, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def setName(name: String): Unit = super.eSetProperty("name", name)

    override def toString: String =
        getType + "([" + getId + "] " + getName + ")"

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassDatatype => this.getName.equals(obj.getName)
            case _ => false
        }
    }

}
