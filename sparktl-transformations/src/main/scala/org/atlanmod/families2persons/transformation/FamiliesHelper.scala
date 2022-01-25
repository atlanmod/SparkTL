package org.atlanmod.families2persons.transformation

import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.FamiliesMember
import org.atlanmod.families2persons.model.families.metamodel.FamiliesMetamodel

object FamiliesHelper {

    def isFemale(model: FamiliesModel, member: FamiliesMember, metamodel: FamiliesMetamodel): Boolean = {
        if (metamodel.familyMother(member, model).isDefined) true
        else {
            if (metamodel.familyDaughter(member, model).isDefined) true
            else false
        }
    }

    def familyName(model: FamiliesModel, member: FamiliesMember, metamodel: FamiliesMetamodel): String = {
        if (metamodel.familyFather(member, model).isDefined) metamodel.familyFather(member, model).get.getLastName()
        else if (metamodel.familyMother(member, model).isDefined) metamodel.familyMother(member, model).get.getLastName()
        else if (metamodel.familySon(member, model).isDefined) metamodel.familySon(member, model).get.getLastName()
        else metamodel.familyDaughter(member, model).get.getLastName()
    }

}
