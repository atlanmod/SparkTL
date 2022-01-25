package org.atlanmod.families2persons.model.families.element

import org.atlanmod.families2persons.model.families.FamiliesElement
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamiliesMember extends FamiliesElement(FamiliesMetamodelNaive.MEMBER){
    def this(firstname: String) = {
        this()
        super.eSetProperty("firstname", firstname)
    }

    def setFirstName(firstname: String) = {
        super.eSetProperty("firstname", firstname)
    }

    def getFirstName(): String = {
        super.eGetProperty("firstname").asInstanceOf[String]
    }

    override def equals(o: Any): Boolean =
        o match {
            case obj: FamiliesMember => obj.getFirstName().equals(this.getFirstName())
            case _ => false
        }

}
