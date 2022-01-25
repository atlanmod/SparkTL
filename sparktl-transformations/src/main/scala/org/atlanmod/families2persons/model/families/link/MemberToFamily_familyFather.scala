package org.atlanmod.families2persons.model.families.link

import org.atlanmod.families2persons.model.families.FamiliesLink
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodelNaive

class MemberToFamily_familyFather (source: FamiliesMember, target: FamiliesFamily)
  extends FamiliesLink( FamiliesMetamodelNaive.FATHER_TO_FAMILY, source, List(target)){

    override def toString: String =
        "(" + source.getFirstName() + "'s " + getType + ": " + target + ")"

    override def getSource: FamiliesMember = source

    override def getTarget: List[FamiliesFamily] = List(target)

    def getTargetFamiliy: FamiliesFamily = target

}
