package org.atlanmod.families2persons.model.families.metamodel
import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}

object FamiliesMetamodelWithMap extends FamiliesMetamodel {

    override def familyMother(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        metamodel.allLinksOfTypeOfElement(member, MOTHER_TO_FAMILY, model) match {
            case Some (optF: List[FamiliesFamily]) => optF.headOption
            case _ => None
        }

    override def familyDaughter(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        metamodel.allLinksOfTypeOfElement(member, DAUGHTER_TO_FAMILY, model) match {
            case Some (optF: List[FamiliesFamily]) => optF.headOption
            case _ => None
        }

    override def familySon(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        metamodel.allLinksOfTypeOfElement(member, SON_TO_FAMILY, model) match {
            case Some (optF: List[FamiliesFamily]) => optF.headOption
            case _ => None
        }

    override def familyFather(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        metamodel.allLinksOfTypeOfElement(member, FATHER_TO_FAMILY, model) match {
            case Some (optF: List[FamiliesFamily]) => optF.headOption
            case _ => None
        }

}
