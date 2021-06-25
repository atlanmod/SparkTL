package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.class2relational.model.IdGenerator
import org.atlanmod.tl.util.ListUtils

class ClassClass extends ClassTypable(ClassMetamodel.CLASS) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String) {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
        super.eSetProperty("abstract", false)
    }

    def this(id:String, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
        super.eSetProperty("abstract", false)
    }

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String, super_ : List[String]) = {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
        super.eSetProperty("abstract", false)
    }

    def this(id:String, name: String, super_ : List[String]) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
        super.eSetProperty("abstract", false)
    }

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String, abs: Boolean) {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
        super.eSetProperty("abstract", abs)
    }

    def this(id:String, name: String, abs: Boolean) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", List(): List[String])
        super.eSetProperty("abstract", abs)
    }

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String, abs: Boolean, super_ : List[String]) = {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
        super.eSetProperty("abstract", abs)
    }

    def this(id:String, name: String, abs: Boolean, super_ : List[String]) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("super", super_)
        super.eSetProperty("abstract", abs)
    }


    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def getSuper: List[String] = super.eGetProperty("super").asInstanceOf[List[String]]
    def isAbstract: Boolean = super.eGetProperty("abstract").asInstanceOf[Boolean]

    def setName(name: String): Unit = super.eSetProperty("name", name)
    def setAbstract(abs: Boolean): Unit = super.eSetProperty("abstract", abs)

    override def toString: String =
       (if (isAbstract) "abstract:" else "") + getType + "([" + getId + (if (getSuper.nonEmpty) " <: " + getSuper.mkString(",") else "") + "] " + getName + ")"

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassClass =>
                this.getName.equals(obj.getName) && ListUtils.eqList(this.getSuper, obj.getSuper)
            case _ => false
        }
    }

}
