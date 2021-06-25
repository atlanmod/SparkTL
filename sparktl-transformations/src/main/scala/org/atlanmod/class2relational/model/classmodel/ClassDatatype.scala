package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.class2relational.model.IdGenerator

class ClassDatatype extends ClassTypable(ClassMetamodel.DATATYPE) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String) {
        this()
        val id: String = IdGenerator.id()
        super.eSetProperty("id", id)
//        super.eSetProperty("isId", false)
    }

    def this(id:String, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
//        super.eSetProperty("isId", false)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def setName(name: String): Unit = super.eSetProperty("name", name)

//    def isId: Boolean = super.eGetProperty("isId").asInstanceOf[Boolean]

    override def toString: String =
        getType + "([" + getId + "] " + getName + ")"

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassDatatype => this.getName.equals(obj.getName)
            case _ => false
        }
    }

}
