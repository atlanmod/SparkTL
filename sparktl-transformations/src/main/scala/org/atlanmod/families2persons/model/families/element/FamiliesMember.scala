package org.atlanmod.families2persons.model.families.element

import org.atlanmod.IdGenerator
import org.atlanmod.families2persons.model.families.FamiliesElement
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamiliesMember extends FamiliesElement(FamiliesMetamodelNaive.MEMBER){

    def this(id: Long, firstname: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("firstname", firstname)
    }

    def this(firstname: String) = {
        this()
        val id = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("firstname", firstname)
    }

    def setFirstName(firstname: String) = {
        super.eSetProperty("firstname", firstname)
    }

    def getFirstName(): String = {
        super.eGetProperty("firstname").asInstanceOf[String]
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]

    override def equals(o: Any): Boolean =
        o match {
            case obj: FamiliesMember => obj.getFirstName().equals(this.getFirstName())
            case _ => false
        }

}
