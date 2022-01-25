package org.atlanmod.families2persons.model.families.link

import org.atlanmod.families2persons.model.families.FamiliesLink
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class FamilyToMember_father (source: FamiliesFamily, target: FamiliesMember)
  extends FamiliesLink (FamiliesMetamodelNaive.FAMILY_TO_FATHER, source, List(target)){

    override def toString: String =
      "(" + source.getLastName() + "'s " + getType + ": " + target + ")"

    override def getSource: FamiliesFamily = source

    override def getTarget: List[FamiliesMember] = List(target)

    def getTargetFather: FamiliesMember = target

}
