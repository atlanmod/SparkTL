package org.atlanmod.class2relational.model.classmodel.element

import org.atlanmod.IdGenerator
import org.atlanmod.class2relational.model.classmodel.ClassElement
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodelNaive

class ClassAttribute extends ClassElement(ClassMetamodelNaive.ATTRIBUTE) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", false)
    }

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String, multi: Boolean){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", multi)
    }

    def this(id: Long, name: String, multi: Boolean){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", multi)
    }

    def this(id: Long, name: String){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("multivalued", false)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
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
