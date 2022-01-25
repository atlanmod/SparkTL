package org.atlanmod.families2persons.model

import org.atlanmod.families2persons.model.families.element.{FamiliesFamily, FamiliesMember}
import org.atlanmod.families2persons.model.families.link._
import org.atlanmod.families2persons.model.families.{FamiliesElement, FamiliesLink, FamiliesModel}

object ModelSamples {

    val random = new scala.util.Random

    private def getOneFamily(id: Int, size: Int): (List[FamiliesElement], List[FamiliesLink]) = {

        val f = new FamiliesFamily("Family"+id)
        val father = new FamiliesMember("father"+id)
        val mother = new FamiliesMember("mother"+id)
        val fam_to_mother = new FamilyToMember_mother(f, mother)
        val fam_to_father = new FamilyToMember_mother(f, father)
        val mother_to_fam = new MemberToFamily_familyMother(mother, f)
        val father_to_fam = new MemberToFamily_familyFather(father, f)

        var elements: List[FamiliesElement] = List(f, father, mother)
        var daughters: List[FamiliesMember] = List()
        var sons: List[FamiliesMember] = List()
        var links: List[FamiliesLink] = List(fam_to_mother,fam_to_father,mother_to_fam,father_to_fam)

        val siblings = size - 3
        for (i <- 1 to siblings){
            if (math.random < 0.5) {
                val daughter = new FamiliesMember("daughter"+id+"_"+i)
                elements = daughter :: elements
                daughters =  daughter :: daughters
                links = new MemberToFamily_familyDaughter(daughter, f) :: links
            }else{
                val son = new FamiliesMember("son"+id+"_"+i)
                elements = son :: elements
                sons =  son :: sons
                links = new MemberToFamily_familySon(son, f) :: links
            }
        }
        links = new FamilyToMember_sons(f, sons) :: new FamilyToMember_daughters(f, daughters) :: links
        (elements, links)
    }

    def getFamiliesModel(size: Int, max_size: Int=6) : FamiliesModel = {
        var acc_size = 0
        var id = 0
        var elements: List[FamiliesElement] = List()
        var links: List[FamiliesLink] = List()
        var a_family: (List[FamiliesElement], List[FamiliesLink]) = null
        while (acc_size <= max_size - 7) {
            val family_size = 2 + random.nextInt((max_size - 2) + 1) + 1
            a_family = getOneFamily(id, family_size)
            acc_size = acc_size + family_size
            id = id + 1
            elements = a_family._1 ++ elements
            links = a_family._2 ++ links
        }
        a_family = getOneFamily(id, max_size - acc_size)
        new FamiliesModel(elements, links)
    }

}
