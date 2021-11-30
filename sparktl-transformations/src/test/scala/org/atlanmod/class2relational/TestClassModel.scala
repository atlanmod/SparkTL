package org.atlanmod.class2relational

import org.atlanmod.class2relational.model.classmodel.{AttributeToClass, AttributeToType, ClassAttribute, ClassClass, ClassDatatype, ClassElement, ClassMetamodel, ClassMetamodelWithMap, ClassModel, ClassToAttributes}
import org.scalatest.funsuite.AnyFunSuite

class TestClassModel  extends AnyFunSuite {

    val type_string = new ClassDatatype("string", "String")
    val type_int = new ClassDatatype("int", "Integer")
    val family = new ClassClass("1", "Family")
    val family_name = new ClassAttribute("2", "name", false)
    val family_members = new ClassAttribute("3", "members", true)
    val person = new ClassClass("4", "Person")
    val person_firstName = new ClassAttribute("5", "firstName", false)
    val person_closestFriend = new ClassAttribute("6", "closestFriend", false)
    val person_emailAddresses = new ClassAttribute("7", "emailAddresses", true)

    def ClassModel: ClassModel = {

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
        model
    }

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

    test("getAttributesOfClass"){
        val res = ClassMetamodel.getAttributeType(family_name, ClassModel)
        val exp = Some(type_string)
        assert(res.equals(exp))
    }

    test("getAttributeOwner"){
        val res = ClassMetamodel.getAttributeOwner(family_name, ClassModel)
        val exp = Some(family)
        assert(res.equals(exp))
    }

    test("getClassAttributes"){
        ClassMetamodel.getClassAttributes(family, ClassModel) match {
            case Some(attrs) =>{
                assert(attrs.length == 2)
                assert(attrs.contains(family_name))
                assert(attrs.contains(family_members))
            }
            case _ => assert(false)
        }
    }

    test("getAttributesOfClassWithMap"){
        val res = ClassMetamodelWithMap.getAttributeType(family_name, ClassModel)
        val exp = Some(type_string)
        assert(res.equals(exp))
    }

    test("getAttributeOwnerWithMap"){
        val res = ClassMetamodelWithMap.getAttributeOwner(family_name, ClassModel)
        val exp = Some(family)
        assert(res.equals(exp))
    }

    test("getClassAttributesWithMap"){
        ClassMetamodelWithMap.getClassAttributes(family, ClassModel) match {
            case Some(attrs) =>{
                assert(attrs.length == 2)
                assert(attrs.contains(family_name))
                assert(attrs.contains(family_members))
            }
            case _ => assert(false)
        }
    }

}
