package org.atlanmod.families2persons

import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.link._
import org.atlanmod.families2persons.model.families.metamodel.{FamiliesMetamodelNaive, FamiliesMetamodelWithMap}
import org.atlanmod.families2persons.transformation.FamiliesHelper
import org.scalatest.funsuite.AnyFunSuite

class TestFamiliesHelper extends AnyFunSuite {

    val La = new FamiliesMember("Laurent")
    val Mi = new FamiliesMember("Michele")
    val My = new FamiliesMember("Mylene")
    val Au = new FamiliesMember("Audrey")
    val Jo = new FamiliesMember("Jolan")
    val Ph = new FamiliesFamily("Philippe")

    def model : FamiliesModel = {
        val Ph_to_father = new FamilyToMember_father(Ph, La)
        val Ph_to_mother = new FamilyToMember_mother(Ph, Mi)
        val Ph_to_sons = new FamilyToMember_sons(Ph, List(Jo))
        val Ph_to_daughters = new FamilyToMember_daughters(Ph, List(My, Au))
        val My_family = new MemberToFamily_familyDaughter(My, Ph)
        val Au_family = new MemberToFamily_familyDaughter(Au, Ph)
        val La_family = new MemberToFamily_familyFather(La, Ph)
        val Mi_family = new MemberToFamily_familyMother(Mi, Ph)
        val Jo_family = new MemberToFamily_familySon(Jo, Ph)

        new FamiliesModel(
            List(La, Mi, My, Au, Jo, Ph),
            List(Ph_to_father, Ph_to_mother, Ph_to_daughters, Ph_to_sons, Mi_family, My_family, Au_family, La_family, Jo_family)
        )
    }

    test("test isFemale with naive metamodel") {
        val metamodel = FamiliesMetamodelNaive
        assert(!FamiliesHelper.isFemale(model, La, metamodel))
        assert(FamiliesHelper.isFemale(model, Mi, metamodel))
        assert(FamiliesHelper.isFemale(model, Au, metamodel))
        assert(FamiliesHelper.isFemale(model, My, metamodel))
        assert(!FamiliesHelper.isFemale(model, Jo, metamodel))
    }

    test("test isFemale with opti metamodel") {
        val metamodel = FamiliesMetamodelWithMap
        assert(!FamiliesHelper.isFemale(model, La, metamodel))
        assert(FamiliesHelper.isFemale(model, Mi, metamodel))
        assert(FamiliesHelper.isFemale(model, Au, metamodel))
        assert(FamiliesHelper.isFemale(model, My, metamodel))
        assert(!FamiliesHelper.isFemale(model, Jo, metamodel))
    }

    test("test familyName with naive metamodel") {
        val metamodel = FamiliesMetamodelNaive
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, La, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Mi, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Jo, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Au, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, My, metamodel)))
    }

    test("test familyName with opti metamodel") {
        val metamodel = FamiliesMetamodelWithMap
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, La, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Mi, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Jo, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, Au, metamodel)))
        assert(Ph.getLastName().equals(FamiliesHelper.familyName(model, My, metamodel)))
    }

}
