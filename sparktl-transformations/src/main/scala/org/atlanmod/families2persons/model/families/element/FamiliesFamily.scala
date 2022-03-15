package org.atlanmod.families2persons.model.families.element

import org.atlanmod.IdGenerator
import org.atlanmod.families2persons.model.families.FamiliesElement
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamiliesFamily extends FamiliesElement(FamiliesMetamodelNaive.FAMILY){

    def this(id: Long, lastname: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("lastname", lastname)
    }

    def this(lastname: String) = {
        this()
        val id = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("lastname", lastname)
    }

    def setLastName(lastname: String) = {
        super.eSetProperty("lastname", lastname)
    }

    def getLastName(): String = {
        super.eGetProperty("lastname").asInstanceOf[String]
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]

    override def equals(o: Any): Boolean =
        o match {
            case obj: FamiliesFamily => obj.getLastName().equals(this.getLastName())
            case _ => false
        }

}
