package org.atlanmod.families2persons.model.families.metamodel
import org.atlanmod.families2persons.model.families.{FamiliesLink, FamiliesModel}
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.link.{MemberToFamily_familyDaughter, MemberToFamily_familyFather, MemberToFamily_familyMother, MemberToFamily_familySon}

object FamiliesMetamodelNaive extends FamiliesMetamodel {

    private def familyMotherOnLinks(member: FamiliesMember, links: Iterator[FamiliesLink]): Option[FamiliesFamily] = {
        while (links.hasNext) {
            val v = links.next()
            if (v.getSource.equals(member) && v.isInstanceOf[MemberToFamily_familyMother])
                return Option(v.asInstanceOf[MemberToFamily_familyMother].getTargetFamiliy)
        }
        None
    }

    private def familyDaughterOnLinks(member: FamiliesMember, links: Iterator[FamiliesLink]): Option[FamiliesFamily]= {
        while (links.hasNext) {
            val v = links.next()
            if (v.getSource.equals(member) && v.isInstanceOf[MemberToFamily_familyDaughter])
                return Option(v.asInstanceOf[MemberToFamily_familyDaughter].getTargetFamiliy)
        }
        None
    }

    private def familySonOnLinks(member: FamiliesMember, links: Iterator[FamiliesLink]): Option[FamiliesFamily] = {
        while (links.hasNext) {
            val v = links.next()
            if (v.getSource.equals(member) && v.isInstanceOf[MemberToFamily_familySon])
                return Option(v.asInstanceOf[MemberToFamily_familySon].getTargetFamiliy)
        }
        None
    }

    private def familyFatherOnLinks(member: FamiliesMember, links: Iterator[FamiliesLink]): Option[FamiliesFamily] = {
        while (links.hasNext) {
            val v = links.next()
            if (v.getSource.equals(member) && v.isInstanceOf[MemberToFamily_familyFather])
                return Option(v.asInstanceOf[MemberToFamily_familyFather].getTargetFamiliy)
        }
        None
    }

    override def familyMother(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        familyMotherOnLinks(member, model.allModelLinks.toIterator)

    override def familyDaughter(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        familyDaughterOnLinks(member, model.allModelLinks.toIterator)

    override def familySon(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        familySonOnLinks(member, model.allModelLinks.toIterator)

    override def familyFather(member: FamiliesMember, model: FamiliesModel): Option[FamiliesFamily] =
        familyFatherOnLinks(member, model.allModelLinks.toIterator)

}
