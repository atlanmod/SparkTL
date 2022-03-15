package org.atlanmod.class2relational.model.relationalmodel.element

import org.atlanmod.IdGenerator
import org.atlanmod.class2relational.model.relationalmodel.RelationalElement
import org.atlanmod.class2relational.model.relationalmodel.metamodel.RelationalMetamodelNaive

class RelationalColumn extends RelationalElement (RelationalMetamodelNaive.COLUMN) {

    @deprecated("Having a random ID can turn inconsistent the output of a transformation", "1.0.1")
    def this(name: String) {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    def this(id: Long, name: String) {
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
            case col: RelationalColumn => col.getName.equals(this.getName)
            case _ => false
        }
    }

}
