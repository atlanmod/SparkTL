package org.atlanmod.model

import org.atlanmod.model.classmodel._
import org.atlanmod.model.relationalmodel._

object ModelGenerator {

    def getClassModelDummy : ClassModel = {
        val family = new ClassClass("0","Family")
        val family_name = new ClassAttribute("1","name", false)
        new ClassModel(List(family, family_name), List(new ClassToAttributes(family, family_name),
            new AttributeToClass(family_name, family)))
    }

    def getRelationalModelDummy : RelationalModel = {
        val family = new RelationalTable("0","Family")
        val family_name = new RelationalColumn("1","name")
        new RelationalModel(List(family, family_name), List(new TableToColumns(family, family_name),
            new ColumnToTable(family_name, family)))
    }

    def getClassModelSample : ClassModel = {
        val family = new ClassClass("1","Family")
        val family_name = new ClassAttribute("2","name", false)
        val family_members = new ClassAttribute("3","members", true)
        val person = new ClassClass("4","Person")
        val person_firstName = new ClassAttribute("5","firstName", false)
        val person_closestFriend = new ClassAttribute("6","closestFriend", false)
        val person_emailAddresses = new ClassAttribute("7","emailAddresses", true)
        val type_string = new ClassDatatype("8","String")
        val type_integer = new ClassDatatype("9", "Integer")

        val elements : List[ClassElement] =
            List(type_string, type_integer,
                family, family_name, family_members,
                person, person_firstName, person_closestFriend, person_emailAddresses)

        val family__to__attributes = new ClassToAttributes(family, List(family_name, family_members))
        val family_name__owner = new AttributeToClass(family_name, family)
        val family_members__owner = new AttributeToClass(family_members, family)
        val family_name__to__type_integer = new AttributeToDatatype(family_name, type_integer)
        val family_members__to__type_string = new AttributeToDatatype(family_members, type_string)

        val person__to__attributes = new ClassToAttributes(person, List(person_firstName, person_closestFriend, person_emailAddresses))
        val person_firstName__owner = new AttributeToClass(person_firstName, person)
        val person_closestFriend__owner = new AttributeToClass(person_closestFriend, person)
        val person_emailAddresses__owner = new AttributeToClass(person_emailAddresses, person)
        val person_firstName__to__type_integer = new AttributeToDatatype(person_firstName, type_integer)
        val person_closestFriend__to__type_string = new AttributeToDatatype(person_closestFriend, type_string)
        val person_emailAddresses__to__type_integer = new AttributeToDatatype(person_emailAddresses, type_integer)

        val links : List[ClassLink] = List(
            family__to__attributes, family_name__owner, family_members__owner, family_members__to__type_string, family_name__to__type_integer,
            person__to__attributes, person_emailAddresses__to__type_integer, person_firstName__to__type_integer, person_closestFriend__to__type_string,
            person_firstName__owner, person_closestFriend__owner, person_emailAddresses__owner
        )

        new ClassModel(elements, links)
    }

    def getRelationalModelSample : RelationalModel = {
        val type_string = new RelationalType("8", "String")
        val type_integer = new RelationalType("9", "Integer")
        val family = new RelationalTable("1", "Family")
        val family_id = new RelationalColumn("1Id", "Id")
        val family_name = new RelationalColumn("2", "name")
        val family_members = new RelationalTable("3pivot", "Family_members")
        val family_members_id = new RelationalColumn("3psrc", "Id")
        val family_members_val = new RelationalColumn("3ptrg", "String")
        val person = new RelationalTable("4", "Person")
        val person_id = new RelationalColumn("4Id", "Id")
        val person_firstname = new RelationalColumn("5", "firstName")
        val person_closestFriend = new RelationalColumn("6", "closestFriend")
        val person_emailAddresses = new RelationalTable("7pivot", "Person_emailAddresses")
        val person_emailAddresses_id = new RelationalColumn("7psrc", "Id")
        val person_emailAddresses_val = new RelationalColumn("7ptrg", "Integer")
        val elements : List[RelationalElement] = List(
            type_string, type_integer,
            family, family_id, family_name, family_members,
            family_members_id, family_members_val,
            person, person_id, person_firstname, person_closestFriend,
            person_emailAddresses, person_emailAddresses_id, person_emailAddresses_val
        )

        val family__to__columns = new TableToColumns(family, List(family_id, family_name))
        val family__to__key = new TableToKeys(family, family_id)
        val family_members__to__columns = new TableToColumns(family_members, List(family_members_id, family_members_val))
        val family_members__to__key = new TableToKeys(family_members, List(family_members_id, family_members_val))

        val person__to__columns = new TableToColumns(person, List(person_id, person_firstname, person_closestFriend))
        val person__to__key = new TableToKeys(person, person_id)
        val person_emailAddresses__to__key = new TableToKeys(person_emailAddresses, List(person_emailAddresses_id, person_emailAddresses_val))
        val person_emailAddresses__to__columns = new TableToColumns(person_emailAddresses, List(person_emailAddresses_id, person_emailAddresses_val))

        // TableToKey /0@col1
        val links : List[RelationalLink] = List(
            family__to__columns, family__to__key, family_members__to__columns, family_members__to__key,
            person__to__columns, person__to__key, person_emailAddresses__to__columns, person_emailAddresses__to__key)
        new RelationalModel(elements, links)
    }

}
