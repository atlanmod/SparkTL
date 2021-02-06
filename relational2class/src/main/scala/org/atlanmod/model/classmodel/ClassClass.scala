package org.atlanmod.model.classmodel

import org.atlanmod.model.IdGenerator
import org.atlanmod.tl.util.ListUtils

class ClassClass extends ClassElement(ClassMetamodel.CLASS) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation")
    def this(name: String) {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
    }

    def this(id:String, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
    }

    @deprecated("Having a random ID can turn inconsistent the output of a transformation")
    def this(name: String, super_ : List[String]) = {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
    }

    def this(id:String,name: String, super_ : List[String]) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def getSuper: List[String] = super.eGetProperty("super").asInstanceOf[List[String]]
    def setName(name: String): Unit = super.eSetProperty("name", name)

    override def toString: String =
        getType + "([" + getId + (if (getSuper.nonEmpty) " <: " + getSuper.mkString(",") else "") + "] " + getName + ")"

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassClass =>
                this.getName.equals(obj.getName) && ListUtils.eqList(this.getSuper, obj.getSuper)
            case _ => false
        }
    }

}
