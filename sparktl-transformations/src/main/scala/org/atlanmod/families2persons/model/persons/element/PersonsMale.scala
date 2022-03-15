package org.atlanmod.families2persons.model.persons.element

import org.atlanmod.IdGenerator
import org.atlanmod.families2persons.model.persons.metamodel.PersonMetamodelNaive

class PersonsMale extends PersonsPerson(PersonMetamodelNaive.MALE) {


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
            case obj: PersonsMale => obj.getFullName().equals(this.getFullName())
            case _ => false
        }
}
