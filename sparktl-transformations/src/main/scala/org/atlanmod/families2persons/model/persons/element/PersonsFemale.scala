package org.atlanmod.families2persons.model.persons.element

import org.atlanmod.families2persons.model.persons.metamodel.PersonMetamodelNaive

class PersonsFemale extends PersonsPerson(PersonMetamodelNaive.FEMALE) {

    def this(fullname: String) = {
        this()
        super.eSetProperty("fullname", fullname)
    }

    def setFullName(fullname: String) = {
        super.eSetProperty("fullname", fullname)
    }

    override def getFullName(): String = eGetProperty("fullname").asInstanceOf[String]

    override def equals(o: Any): Boolean =
        o match {
            case obj: PersonsFemale => obj.getFullName().equals(this.getFullName())
            case _ => false
        }
}

