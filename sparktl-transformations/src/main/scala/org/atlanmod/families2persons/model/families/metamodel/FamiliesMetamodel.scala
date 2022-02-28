package org.atlanmod.families2persons.model.families.metamodel

import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait FamiliesMetamodel extends Serializable {

    final val FAMILY = "Family"
    final val MEMBER = "Member"
    final val FAMILY_TO_DAUGHTERS = "daughters"
    final val FAMILY_TO_SONS = "sons"
    final val FAMILY_TO_MOTHER = "mother"
    final val FAMILY_TO_FATHER = "father"
    final val FATHER_TO_FAMILY = "familyFather"
    final val MOTHER_TO_FAMILY = "familyMother"
    final val SON_TO_FAMILY = "familySon"
    final val DAUGHTER_TO_FAMILY = "familyDaughter"


    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("FamiliesMetamodel")

    def familyMother(member: FamiliesMember, model: FamiliesModel) : Option[FamiliesFamily]
    def familyDaughter(member: FamiliesMember, model: FamiliesModel) : Option[FamiliesFamily]
    def familySon(member: FamiliesMember, model: FamiliesModel) : Option[FamiliesFamily]
    def familyFather(member: FamiliesMember, model: FamiliesModel) : Option[FamiliesFamily]
}