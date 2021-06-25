package org.atlanmod.class2relational.model

import org.atlanmod.class2relational.model.classmodel._
import org.atlanmod.class2relational.model.relationalmodel._

object ModelSamples {

    def getClassModelSingle: ClassModel = {
        val c1 = new ClassClass("0", "class1")
        val sv = new ClassAttribute("1", "sv", false)
        //        val mv = new ClassAttribute("2","mv", true)
        val t = new ClassDatatype("int", "int")
        val elements = List(c1, sv, t)
        val c1_attributes = new ClassToAttributes(c1, List(sv))
        val sv_owner = new AttributeToClass(sv, c1)
        //        val mv_owner = new AttributeToClass(mv, c1)
        val sv_type = new AttributeToType(sv, t)
        //        val mv_type = new AttributeToType(mv, t)
        val links = List(c1_attributes, sv_owner, sv_type)
        new ClassModel(elements, links)
    }

    def getRelationalModelSingle: RelationalModel = {
        val t = new RelationalType("int", "int")
        val c1 = new RelationalTable("0", "class1")
        val c1id = new RelationalColumn("0id", "id")
        val sv = new RelationalColumn("1", "sv")
        //        val pivot = new RelationalTable("2pivot", "mv")
        //        val mvsrc = new RelationalColumn("2src", "id")
        //        val mvtrg = new RelationalColumn("2trg", "int")
        val elements = List(t, c1, c1id, sv)

        val c1columns = new TableToColumns(c1, List(c1id, sv))
        val c1key = new TableToKeys(c1, c1id)
        val c1idtable = new ColumnToTable(c1id, c1)
        val a = new ColumnToTable(sv, c1)
        val b = new ColumnToType(sv, t)
        //        val c = new TableToKeys(pivot, List(mvsrc, mvtrg))
        //        val d = new TableToColumns(pivot, List(mvsrc, mvtrg))
        //        val e = new ColumnToTable(mvsrc, pivot)
        //        val f = new ColumnToTable(mvtrg, pivot)
        val links = List(c1columns, c1key, c1idtable, a, b)

        new RelationalModel(elements, links)
    }


    def getClassModelDummy: ClassModel = {
        val family = new ClassClass("0", "Family")
        val family_name = new ClassAttribute("1", "name", false)
        new ClassModel(List(family, family_name), List(new ClassToAttributes(family, family_name),
            new AttributeToClass(family_name, family)))
    }

    def getRelationalModelDummy: RelationalModel = {
        val family = new RelationalTable("0", "Family")
        val family_name = new RelationalColumn("1", "name")
        new RelationalModel(List(family, family_name), List(new TableToColumns(family, family_name),
            new ColumnToTable(family_name, family)))
    }

    def getClassModelSample: ClassModel = {
        /* <DataType name="String"/>
          <DataType name="Integer"/>
          <Class name="Family">
              <attr name="name" multiValued="false" type="String"/>
              <attr name="members" multiValued="true" type="Person"/>
          </Class>
          <Class name="Person">
              <attr name="firstName" multiValued="false" type="String"/>
              <attr name="closestFriend" multiValued="false" type="Person"/>
              <attr name="emailAddresses" multiValued="true" type="String"/>
          </Class> */
        val type_string = new ClassDatatype("string", "String")
        val type_int = new ClassDatatype("int", "Integer")
        val family = new ClassClass("1", "Family")
        val family_name = new ClassAttribute("2", "name", false)
        val family_members = new ClassAttribute("3", "members", true)
        val person = new ClassClass("4", "Person")
        val person_firstName = new ClassAttribute("5", "firstName", false)
        val person_closestFriend = new ClassAttribute("6", "closestFriend", false)
        val person_emailAddresses = new ClassAttribute("7", "emailAddresses", true)

        val elements: List[ClassElement] = List(type_string, type_int, family, family_name, family_members,
            person, person_firstName, person_closestFriend, person_emailAddresses)

        // Attributes of classes
        val family__to__attributes = new ClassToAttributes(family, List(family_name, family_members))
        val person__to__attributes = new ClassToAttributes(person, List(person_firstName, person_closestFriend,
            person_emailAddresses))

        // Owning links
        val family_name__owner = new AttributeToClass(family_name, family)
        val family_members__owner = new AttributeToClass(family_members, family)
        val person_firstName__owner = new AttributeToClass(person_firstName, person)
        val person_closestFriend__owner = new AttributeToClass(person_closestFriend, person)
        val person_emailAddresses__owner = new AttributeToClass(person_emailAddresses, person)

        // Types of attributes
        val family_name__to__type_string = new AttributeToType(family_name, type_string)
        val firstName__to__type_string = new AttributeToType(person_firstName, type_string)
        val closestFriend__to__type_person = new AttributeToType(person_closestFriend, person)
        val members__to__type_person = new AttributeToType(family_members, person)
        val emailAddresses__to__type_string = new AttributeToType(person_emailAddresses, type_string)

        val links = List(family__to__attributes, person__to__attributes,
            family_name__owner, family_members__owner, person_firstName__owner, person_closestFriend__owner, person_emailAddresses__owner,
            family_name__to__type_string, firstName__to__type_string, closestFriend__to__type_person, members__to__type_person,
            emailAddresses__to__type_string)

        val model = new ClassModel(elements, links)
        val tmp = ClassMetamodel.getAttributeOwner(family_members, model)
        model
    }

    def getClassModelSample_randomId: ClassModel = {
        /* <DataType name="String"/>
           <DataType name="Integer"/>
           <Class name="Family">
               <attr name="name" multiValued="false" type="String"/>
               <attr name="members" multiValued="true" type="Person"/>
           </Class>
           <Class name="Person">
               <attr name="firstName" multiValued="false" type="String"/>
               <attr name="closestFriend" multiValued="false" type="Person"/>
               <attr name="emailAddresses" multiValued="true" type="String"/>
           </Class> */
        val type_string = new ClassDatatype("String")
        val type_int = new ClassDatatype("Integer")
        val family = new ClassClass("Family")
        val family_name = new ClassAttribute("name", false)
        val family_members = new ClassAttribute("members", true)
        val person = new ClassClass("Person")
        val person_firstName = new ClassAttribute("firstName", false)
        val person_closestFriend = new ClassAttribute("closestFriend", false)
        val person_emailAddresses = new ClassAttribute("emailAddresses", true)

        val elements: List[ClassElement] = List(type_string, type_int, family, family_name, family_members,
            person, person_firstName, person_closestFriend, person_emailAddresses)

        // Attributes of classes
        val family__to__attributes = new ClassToAttributes(family, List(family_name, family_members))
        val person__to__attributes = new ClassToAttributes(person, List(person_firstName, person_closestFriend,
            person_emailAddresses))

        // Owning links
        val family_name__owner = new AttributeToClass(family_name, family)
        val family_members__owner = new AttributeToClass(family_members, family)
        val person_firstName__owner = new AttributeToClass(person_firstName, person)
        val person_closestFriend__owner = new AttributeToClass(person_closestFriend, person)
        val person_emailAddresses__owner = new AttributeToClass(person_emailAddresses, person)

        // Types of attributes
        val family_name__to__type_string = new AttributeToType(family_name, type_string)
        val firstName__to__type_string = new AttributeToType(person_firstName, type_string)
        val closestFriend__to__type_person = new AttributeToType(person_closestFriend, person)
        val members__to__type_person = new AttributeToType(family_members, person)
        val emailAddresses__to__type_string = new AttributeToType(person_emailAddresses, type_string)

        val links = List(family__to__attributes, person__to__attributes,
            family_name__owner, family_members__owner, person_firstName__owner, person_closestFriend__owner, person_emailAddresses__owner,
            family_name__to__type_string, firstName__to__type_string, closestFriend__to__type_person, members__to__type_person,
            emailAddresses__to__type_string)

        val model = new ClassModel(elements, links)
        val tmp = ClassMetamodel.getAttributeOwner(family_members, model)
        model
    }

    def getRelationalModelSample: RelationalModel = {
        /* <Type @String name="String"/>
           <Type @Integer name="Integer"/>
           <Table name="Family" key="@FamilyId">
              <col name="name" type="@String"/>
              <col @FamilyId name="Id"/>
           </Table>
           <Table @Person name="Person" key="@PersonId">
              <col name="firstName" type="@String"/>
              <col name="closestFriend" type="@Person"/>
              <col @PersonId name="Id"/>
           </Table>
           <Table name="Family_members" key="@Family_members_Id @Family_members_Type">
              <col @Family_members_Id  name="Id"/>
              <col @Family_members_Type name="Person" type="@Person"/>
           </Table>
           <Table name="Person_emailAddresses" key="@Person_emailAddresses_Id @Person_emailAddresses_Type">
              <col @Person_emailAddresses_Id name="Id" />
              <col @Person_emailAddresses_Type name="String" type="@String"/>
           </Table> */
        val type_string = new RelationalType("string", "String")
        val type_int = new RelationalType("int", "Integer")
        val family = new RelationalTable("1", "Family")
        val family_name = new RelationalColumn("2", "name")
        val family_id = new RelationalColumn("1Id", "Id")
        val person = new RelationalTable("4", "Person")
        val person_firstname = new RelationalColumn("5", "firstName")
        val person_closestFriend = new RelationalColumn("6", "closestFriend")
        val person_id = new RelationalColumn("4Id", "Id")
        val family_members = new RelationalTable("3pivot", "Family_members")
        val family_members_id = new RelationalColumn("3psrc", "Id")
        val family_members_type = new RelationalColumn("3ptrg", "Person")
        val person_emailAddresses = new RelationalTable("7pivot", "Person_emailAddresses")
        val person_emailAddresses_id = new RelationalColumn("7psrc", "Id")
        val person_emailAddresses_type = new RelationalColumn("7ptrg", "String")

        val elements = List(type_string, type_int,
            family, family_name, family_id,
            person, person_firstname, person_closestFriend, person_id,
            family_members, family_members_id, family_members_type,
            person_emailAddresses, person_emailAddresses_id, person_emailAddresses_type)

        // Table to columns
        val family__to__columns = new TableToColumns(family, List(family_name, family_id))
        val person__to__columns = new TableToColumns(person, List(person_firstname, person_closestFriend, person_id))
        val family_members__to__columns = new TableToColumns(family_members, List(family_members_id, family_members_type))
        val person_emailAddresses__to__columns = new TableToColumns(person_emailAddresses, List(person_emailAddresses_id, person_emailAddresses_type))

        // Column owner
        val family_name__to__family = new ColumnToTable(family_name, family)
        val family_id__to__family = new ColumnToTable(family_id, family)
        val person_firstname__to__person = new ColumnToTable(person_firstname, person)
        val person_closestFriend__to__person = new ColumnToTable(person_closestFriend, person)
        val person_id__to__person = new ColumnToTable(person_id, person)
        val family_members_id__to__family_members = new ColumnToTable(family_members_id, family_members)
        val family_members_type__to__family_members = new ColumnToTable(family_members_type, family_members)
        val person_emailAddresses_id__to__person_emailAddresses = new ColumnToTable(person_emailAddresses_id, person_emailAddresses)
        val person_emailAddresses_type__to__person_emailAddresses = new ColumnToTable(person_emailAddresses_type, person_emailAddresses)

        // Key(s) of table
        val family__to__key = new TableToKeys(family, family_id)
        val person__to__key = new TableToKeys(person, person_id)
        val family_members__to__keys = new TableToKeys(family_members, List(family_members_id, family_members_type))
        val person_emailAddresses__to__keys = new TableToKeys(person_emailAddresses, List(person_emailAddresses_id, person_emailAddresses_type))

        // Types of Columns
        val family_name__to__type_string = new ColumnToType(family_name, type_string)
        // val family_id__to__type_int = new ColumnToType(family_id, type_int)
        val person_firstname__to__type_string = new ColumnToType(person_firstname, type_string)
        val person_closestFriend__to__type_person = new ColumnToType(person_closestFriend, person)
        // val person_id__to__type_int = new ColumnToType(person_id, type_int)
        val family_members_type__to__type_person = new ColumnToType(family_members_type, person)
        val person_emailAddresses_type__to__type_string = new ColumnToType(person_emailAddresses_type, type_string)
        // val family_members_id__to__type_int = new ColumnToType(family_members_id, type_int)
        // val person_emailAddresses_id__to__type_int = new ColumnToType(person_emailAddresses_id, type_int)

        val links = List(family__to__columns, person__to__columns, family_members__to__columns, person_emailAddresses__to__columns,
            family_name__to__family, family_id__to__family, person_firstname__to__person, person_closestFriend__to__person,
            person_id__to__person, family_members_id__to__family_members, family_members_type__to__family_members,
            person_emailAddresses_id__to__person_emailAddresses, person_emailAddresses_type__to__person_emailAddresses,
            family__to__key, person__to__key, family_members__to__keys, person_emailAddresses__to__keys,
            family_name__to__type_string, person_firstname__to__type_string, person_closestFriend__to__type_person,
            family_members_type__to__type_person, person_emailAddresses_type__to__type_string
            //, person_id__to__type_int, family_id__to__type_int, family_members_id__to__type_int,
            // person_emailAddresses_id__to__type_int
        )

        new RelationalModel(elements, links)
    }

}
