package org.atlanmod.families2persons.model.families.element

import org.atlanmod.families2persons.model.families.FamiliesElement
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamiliesFamily extends FamiliesElement(FamiliesMetamodelNaive.FAMILY){

    def this(lastname: String) = {
        this()
        super.eSetProperty("lastname", lastname)
    }

    def setLastName(lastname: String) = {
        super.eSetProperty("lastname", lastname)
    }

    def getLastName(): String = {
        super.eGetProperty("lastname").asInstanceOf[String]
    }

    override def equals(o: Any): Boolean =
        o match {
            case obj: FamiliesFamily => obj.getLastName().equals(this.getLastName())
            case _ => false
        }

}
