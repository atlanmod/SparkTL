package org.atlanmod.families2persons.model.families.link

import org.atlanmod.families2persons.model.families.FamiliesLink
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamilyToMember_sons (source: FamiliesFamily, target: List[FamiliesMember])
  extends FamiliesLink(FamiliesMetamodelNaive.FAMILY_TO_SONS, source, target){

    override def toString: String =
        "(" + source.getLastName() + "'s " + getType + ": " + target.mkString(",") + ")"

    override def getSource: FamiliesFamily = source

    override def getTarget: List[FamiliesMember] = target

}
