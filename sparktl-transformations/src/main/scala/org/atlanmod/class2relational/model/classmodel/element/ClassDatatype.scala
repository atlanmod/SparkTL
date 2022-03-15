package org.atlanmod.class2relational.model.classmodel.element

import org.atlanmod.IdGenerator
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodelNaive

class ClassDatatype extends ClassClassifier(ClassMetamodelNaive.DATATYPE) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String) {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
    }

    def this(id:Long, name: String) {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
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
