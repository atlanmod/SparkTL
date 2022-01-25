package org.atlanmod.families2persons

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.families2persons.model.families.FamiliesModel
import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.link.{FamilyToMember_daughters, FamilyToMember_father, FamilyToMember_mother, FamilyToMember_sons, MemberToFamily_familyDaughter, MemberToFamily_familyFather, MemberToFamily_familyMother, MemberToFamily_familySon}
import org.atlanmod.families2persons.model.families.metamodel.{FamiliesMetamodelNaive, FamiliesMetamodelWithMap}
import org.atlanmod.families2persons.model.persons.element.{PersonsFemale, PersonsMale}
import org.atlanmod.families2persons.transformation.Families2Persons
import org.atlanmod.tl.engine.{TransformationParallel, TransformationSequential}
import org.scalatest.funsuite.AnyFunSuite

class TestFamilies2Persons extends AnyFunSuite {

    var spark: SparkContext = null
    def model : FamiliesModel = {
        val La = new FamiliesMember("Laurent")
        val Mi = new FamiliesMember("Michele")
        val My = new FamiliesMember("Mylene")
        val Au = new FamiliesMember("Audrey")
        val Jo = new FamiliesMember("Jolan")
        val Ph = new FamiliesFamily("Philippe")

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
    def getSpark : SparkContext = {
        if (spark == null) {
            spark = {
                val conf = new SparkConf()
                conf.setAppName("TestFamilies2Persons")
                conf.setMaster("local[2]")
                new SparkContext(conf)
            }
        }
        spark
    }

    test("test Families2Persons sequential with naive metamodel") {
        val metamodel = FamiliesMetamodelNaive
        val tr = Families2Persons.families2persons(metamodel)
        val res = TransformationSequential.execute(tr, model, metamodel.metamodel)
        val exp = List(new PersonsMale("Jolan Philippe"), new PersonsMale("Laurent Philippe"),
            new PersonsFemale("Audrey Philippe"), new PersonsFemale("Michele Philippe"), new PersonsFemale("Mylene Philippe"))
        for (e <- exp) assert(res.allModelElements.contains(e))
    }

    test("test Families2Persons sequential with optimal metamodel") {
        val metamodel = FamiliesMetamodelWithMap
        val tr = Families2Persons.families2persons(metamodel)
        val res = TransformationSequential.execute(tr, model, metamodel.metamodel)
        val exp = List(new PersonsMale("Jolan Philippe"), new PersonsMale("Laurent Philippe"),
            new PersonsFemale("Audrey Philippe"), new PersonsFemale("Michele Philippe"), new PersonsFemale("Mylene Philippe"))
        for (e <- exp) assert(res.allModelElements.contains(e))
    }

    test("test Families2Persons parallel with naive metamodel") {
        val metamodel = FamiliesMetamodelNaive
        val tr = Families2Persons.families2persons(metamodel)
        val res = TransformationParallel.execute(tr, model, metamodel.metamodel, 2, getSpark)

        val exp = List(new PersonsMale("Jolan Philippe"), new PersonsMale("Laurent Philippe"),
            new PersonsFemale("Audrey Philippe"), new PersonsFemale("Michele Philippe"), new PersonsFemale("Mylene Philippe"))
        for (e <- exp) assert(res.allModelElements.contains(e))
    }

    test("test Families2Persons parallel with optimal metamodel") {
        val metamodel = FamiliesMetamodelWithMap
        val tr = Families2Persons.families2persons(metamodel)
        val res = TransformationParallel.execute(tr, model, metamodel.metamodel, 2, getSpark)
        val exp = List(new PersonsMale("Jolan Philippe"), new PersonsMale("Laurent Philippe"),
            new PersonsFemale("Audrey Philippe"), new PersonsFemale("Michele Philippe"), new PersonsFemale("Mylene Philippe"))
        for (e <- exp) assert(res.allModelElements.contains(e))
    }

}