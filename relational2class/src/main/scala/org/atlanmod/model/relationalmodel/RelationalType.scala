package org.atlanmod.model.relationalmodel

import org.atlanmod.model.IdGenerator

class RelationalType extends RelationalElement(RelationalMetamodel.TYPE){

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
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def setName(name: String): Unit = super.eSetProperty("name", name)

    override def toString: String =
        getType + "([" + getId + "] " + getName + ")"

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: RelationalType => this.getName.equals(obj.getName)
            case _ => false
        }
    }

}