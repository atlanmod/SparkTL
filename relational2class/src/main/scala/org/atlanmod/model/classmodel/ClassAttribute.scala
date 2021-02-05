package org.atlanmod.model.classmodel

import org.atlanmod.model.IdGenerator

class ClassAttribute extends ClassElement(ClassMetamodel.ATTRIBUTE) {

    def this(name: String){
        this()
        super.eSetProperty("id", IdGenerator.id())
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", false)
    }

    def this(name: String, multi: Boolean){
        this()
        super.eSetProperty("id", IdGenerator.id())
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", multi)
    }

    def this(id: String, name: String, multi: Boolean){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", multi)
    }

    def this(id: String, name: String){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", false)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def setName(name: String): Unit = super.eSetProperty("name", name)
    def isMultivalued: Boolean = super.eGetProperty("multivalued").asInstanceOf[Boolean]
    def setMultivalued(multi: Boolean): Unit = super.eSetProperty("multivalued", multi)

    override def toString: String =
        getType + "([" + getId + "] " + getName + (if(isMultivalued) "multivalued)" else ")")

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassAttribute =>
                this.getName.equals(obj.getName) && this.isMultivalued.equals(obj.isMultivalued)
            case _ => false
        }
    }

}
