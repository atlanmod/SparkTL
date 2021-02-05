package org.atlanmod.model

import org.atlanmod.model.classmodel._
import org.atlanmod.model.relationalmodel.{RelationalElement, RelationalLink, RelationalModel}

object ModelGenerator {

    def getClassModelSample : ClassModel = {

        val type_1 = new ClassDatatype("/1")
        val type_2 = new ClassDatatype("/2")
        val type_string = new ClassDatatype("String")
        val type_integer = new ClassDatatype("Integer")
        val family = new ClassClass("Family")
        val family_name = new ClassAttribute("name", false)
        val family_members = new ClassAttribute("members", true)
        val person = new ClassClass("Person")
        val person_firstName = new ClassAttribute("firstName", false)
        val person_closestFriend = new ClassAttribute("closestFriend", false)
        val person_emailAddresses = new ClassAttribute("emailAddresses", true)

        val elements : List[ClassElement] =
            List(type_1, type_2, type_string, type_integer,
                family, family_name, family_members,
                person, person_firstName, person_closestFriend, person_emailAddresses)

        val family__to__attributes = new ClassToAttributes(family, List(family_name, family_members))
        val family_name__owner = new AttributeToClass(family_name, family)
        val family_members__owner = new AttributeToClass(family_members, family)
        val family_name__to__type_1 = new AttributeToDatatype(family_name, type_1)
        val family_members__to__type_1 = new AttributeToDatatype(family_members, type_2)

        val person__to__attributes = new ClassToAttributes(person, List(person_firstName, person_closestFriend, person_emailAddresses))
        val person_firstName__owner = new AttributeToClass(person_firstName, person)
        val person_closestFriend__owner = new AttributeToClass(person_closestFriend, person)
        val person_emailAddresses__owner = new AttributeToClass(person_emailAddresses, person)
        val person_firstName__to__type_2 = new AttributeToDatatype(person_firstName, type_2)
        val person_closestFriend__to__type_1 = new AttributeToDatatype(person_closestFriend, type_1)
        val person_emailAddresses__to__type_2 = new AttributeToDatatype(person_emailAddresses, type_2)

        val links : List[ClassLink] = List(
            family__to__attributes, family_name__owner, family_members__owner, family_members__to__type_1, family_name__to__type_1,
            person__to__attributes, person_emailAddresses__to__type_2, person_firstName__to__type_2, person_closestFriend__to__type_1,
            person_firstName__owner, person_closestFriend__owner, person_emailAddresses__owner
        )

        new ClassModel(elements, links)
    }

    def getRelationalModelSample : RelationalModel = {
        // table Family
        val elements : List[RelationalElement] = List()

        // TableToKey /0@col1
        val links : List[RelationalLink] = List()
        new RelationalModel(elements, links)
    }
}
