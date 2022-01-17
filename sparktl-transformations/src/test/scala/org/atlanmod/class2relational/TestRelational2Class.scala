package org.atlanmod.class2relational

import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassDatatype}
import org.atlanmod.class2relational.model.classmodel.link.{AttributeToType, ClassToAttributes}
import org.atlanmod.class2relational.model.classmodel.metamodel.{ClassMetamodelNaive, ClassMetamodelWithMap}
import org.atlanmod.class2relational.model.classmodel.{ClassElement, ClassLink, ClassModel}
import org.atlanmod.class2relational.model.relationalmodel.RelationalModel
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalClassifier, RelationalType}
import org.atlanmod.class2relational.model.relationalmodel.link.{ColumnToType, TableToColumns, TableToKeys}
import org.atlanmod.class2relational.model.relationalmodel.metamodel.{RelationalMetamodelNaive, RelationalMetamodelWithMap}
import org.atlanmod.class2relational.transformation.Relational2Class
import org.atlanmod.tl.engine.TransformationSequential
import org.atlanmod.tl.util.ListUtils
import org.scalatest.funsuite.AnyFunSuite

class TestRelational2Class extends AnyFunSuite {

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

    def getClassModelSample: ClassModel = {
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

        // Types of attributes
        val family_name__to__type_string = new AttributeToType(family_name, type_string)
        val firstName__to__type_string = new AttributeToType(person_firstName, type_string)
        val closestFriend__to__type_person = new AttributeToType(person_closestFriend, person)
        val members__to__type_person = new AttributeToType(family_members, person)
        val emailAddresses__to__type_string = new AttributeToType(person_emailAddresses, type_string)

        val links = List(family__to__attributes, person__to__attributes,
            family_name__to__type_string, firstName__to__type_string, closestFriend__to__type_person, members__to__type_person,
            emailAddresses__to__type_string)

        val model = new ClassModel(elements, links)
        model
    }

    def getRelationalModelSample : RelationalModel = {

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
//            family_name__to__family, family_id__to__family, person_firstname__to__person, person_closestFriend__to__person,
//            person_id__to__person, family_members_id__to__family_members, family_members_type__to__family_members,
//            person_emailAddresses_id__to__person_emailAddresses, person_emailAddresses_type__to__person_emailAddresses,
            family__to__key, person__to__key, family_members__to__keys, person_emailAddresses__to__keys,
            family_name__to__type_string, person_firstname__to__type_string, person_closestFriend__to__type_person,
            family_members_type__to__type_person, person_emailAddresses_type__to__type_string
            //, person_id__to__type_int, family_id__to__type_int, family_members_id__to__type_int,
            // person_emailAddresses_id__to__type_int
        )

        new RelationalModel(elements, links)
    }

    def classmodel : ClassModel = {
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
        getClassModelSample
    }

    def relationalmodel : RelationalModel = {
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
        getRelationalModelSample
    }

    test("test getTableColumns naive"){
        val meta = RelationalMetamodelNaive
        var res = meta.getTableColumns(family, relationalmodel).getOrElse(List())
        var exp = List(family_name, family_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(person, relationalmodel).getOrElse(List())
        exp = List(person_firstname, person_closestFriend, person_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(family_members, relationalmodel).getOrElse(List())
        exp = List(family_members_id, family_members_type)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(person_emailAddresses, relationalmodel).getOrElse(List())
        exp = List(person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getTableKeys naive"){
        val meta = RelationalMetamodelNaive
        var res = meta.getTableKeys(person_emailAddresses, relationalmodel).getOrElse(List())
        var exp = List(person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(family, relationalmodel).getOrElse(List())
        exp = List(family_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(person, relationalmodel).getOrElse(List())
        exp = List(person_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(family_members, relationalmodel).getOrElse(List())
        exp = List(family_members_id, family_members_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getKeyOf naive"){
        val meta = RelationalMetamodelNaive
        var res = meta.getKeyOf(person_emailAddresses_id, relationalmodel)
        var exp : Option[RelationalTable] = Some(person_emailAddresses)
        assert(res.equals(exp))
        res = meta.getKeyOf(person_emailAddresses_type, relationalmodel)
        exp = Some(person_emailAddresses)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_name, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(family_id, relationalmodel)
        exp = Some(family)
        assert(res.equals(exp))
        res = meta.getKeyOf(person_firstname, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(person_closestFriend, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(person_id, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_members_id, relationalmodel)
        exp = Some(family_members)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_members_type, relationalmodel)
        exp = Some(family_members)
        assert(res.equals(exp))
    }

    test("test getColumnType naive"){
        val meta = RelationalMetamodelNaive
        var res = meta.getColumnType(person_emailAddresses_type, relationalmodel)
        var exp : Option[RelationalClassifier] = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(person_firstname, relationalmodel)
        exp = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(family_name, relationalmodel)
        exp = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(person_closestFriend, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
        res = meta.getColumnType(family_members_type, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
    }

    test("test getAllTable naive"){
        val meta = RelationalMetamodelNaive
        val res = meta.getAllTable(relationalmodel)
        val exp = List(person_emailAddresses, family, person, family_members)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllColumns naive"){
        val meta = RelationalMetamodelNaive
        val res = meta.getAllColumns(relationalmodel)
        val exp = List(family_name, family_id, person_firstname, person_closestFriend, person_id,
            family_members_id, family_members_type, person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllType naive"){
        val meta = RelationalMetamodelNaive
        val res = meta.getAllType(relationalmodel)
        val exp = List(type_string, type_int)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllTypable naive"){
        val meta = RelationalMetamodelNaive
        val res = meta.getAllTypable(relationalmodel)
        val exp = List(type_string, type_int, person_emailAddresses, family, person, family_members)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getTableColumns with map"){
        val meta = RelationalMetamodelWithMap
        var res = meta.getTableColumns(family, relationalmodel).getOrElse(List())
        var exp = List(family_name, family_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(person, relationalmodel).getOrElse(List())
        exp = List(person_firstname, person_closestFriend, person_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(family_members, relationalmodel).getOrElse(List())
        exp = List(family_members_id, family_members_type)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableColumns(person_emailAddresses, relationalmodel).getOrElse(List())
        exp = List(person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getTableKeys with map"){
        val meta = RelationalMetamodelWithMap
        var res = meta.getTableKeys(person_emailAddresses, relationalmodel).getOrElse(List())
        var exp = List(person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(family, relationalmodel).getOrElse(List())
        exp = List(family_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(person, relationalmodel).getOrElse(List())
        exp = List(person_id)
        assert(ListUtils.weak_eqList(res, exp))
        res = meta.getTableKeys(family_members, relationalmodel).getOrElse(List())
        exp = List(family_members_id, family_members_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getKeyOf with map"){
        val meta = RelationalMetamodelWithMap
        var res = meta.getKeyOf(person_emailAddresses_id, relationalmodel)
        var exp : Option[RelationalTable] = Some(person_emailAddresses)
        assert(res.equals(exp))
        res = meta.getKeyOf(person_emailAddresses_type, relationalmodel)
        exp = Some(person_emailAddresses)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_name, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(family_id, relationalmodel)
        exp = Some(family)
        assert(res.equals(exp))
        res = meta.getKeyOf(person_firstname, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(person_closestFriend, relationalmodel)
        exp = None
        assert(res.equals(exp))
        res = meta.getKeyOf(person_id, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_members_id, relationalmodel)
        exp = Some(family_members)
        assert(res.equals(exp))
        res = meta.getKeyOf(family_members_type, relationalmodel)
        exp = Some(family_members)
        assert(res.equals(exp))
    }

    test("test getColumnType with map"){
        val meta = RelationalMetamodelWithMap
        var res = meta.getColumnType(person_emailAddresses_type, relationalmodel)
        var exp : Option[RelationalClassifier] = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(person_firstname, relationalmodel)
        exp = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(family_name, relationalmodel)
        exp = Some(type_string)
        assert(res.equals(exp))
        res = meta.getColumnType(person_closestFriend, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
        res = meta.getColumnType(family_members_type, relationalmodel)
        exp = Some(person)
        assert(res.equals(exp))
    }

    test("test getAllTable with map"){
        val meta = RelationalMetamodelWithMap
        val res = meta.getAllTable(relationalmodel)
        val exp = List(person_emailAddresses, family, person, family_members)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllColumns with map"){
        val meta = RelationalMetamodelWithMap
        val res = meta.getAllColumns(relationalmodel)
        val exp = List(family_name, family_id, person_firstname, person_closestFriend, person_id,
            family_members_id, family_members_type, person_emailAddresses_id, person_emailAddresses_type)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllType with map"){
        val meta = RelationalMetamodelWithMap
        val res = meta.getAllType(relationalmodel)
        val exp = List(type_string, type_int)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test("test getAllTypable with map"){
        val meta = RelationalMetamodelWithMap
        val res = meta.getAllTypable(relationalmodel)
        val exp = List(type_string, type_int, person_emailAddresses, family, person, family_members)
        assert(ListUtils.weak_eqList(res, exp))
    }

    test ("test relational2class naive") {
        val r2c = Relational2Class.relational2class(RelationalMetamodelNaive, ClassMetamodelNaive)
        val res = TransformationSequential.execute(r2c, relationalmodel, RelationalMetamodelNaive.metamodel)
        val res_model = new ClassModel(res.allModelElements.asInstanceOf[List[ClassElement]],res.allModelLinks.asInstanceOf[List[ClassLink]])
        val exp = getClassModelSample
        assert(res_model.weak_equals(exp))
    }

    test ("test relational2class with map") {
        val r2c = Relational2Class.relational2class(RelationalMetamodelWithMap, ClassMetamodelWithMap)
        val res = TransformationSequential.execute(r2c, relationalmodel, RelationalMetamodelWithMap.metamodel)
        val res_model = new ClassModel(res.allModelElements.asInstanceOf[List[ClassElement]],res.allModelLinks.asInstanceOf[List[ClassLink]])
        val exp = getClassModelSample
        assert(res_model.weak_equals(exp))
    }

}
