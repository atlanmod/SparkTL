package org.atlanmod.families2persons.model.persons.element

import org.atlanmod.IdGenerator
import org.atlanmod.families2persons.model.persons.metamodel.PersonMetamodelNaive

class PersonsFemale extends PersonsPerson(PersonMetamodelNaive.FEMALE) {

    def this(id: Long, fullname: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("fullname", fullname)
    }

    def this(fullname: String) = {
        this()
        val id = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("fullname", fullname)
    }

    def setFullName(fullname: String) = {
        super.eSetProperty("fullname", fullname)
    }

    override def getFullName(): String = eGetProperty("fullname").asInstanceOf[String]
    override def getId: Long = eGetProperty("id").asInstanceOf[Long]

    override def equals(o: Any): Boolean =
        o match {
            case obj: PersonsFemale => obj.getFullName().equals(this.getFullName())
            case _ => false
        }
}

