package org.atlanmod.model

import org.atlanmod.tl.sequential.io.EMFTool
import org.eclipse.emf.ecore.{EClass, EObject, EPackage, EReference}
import org.eclipse.emf.ecore.util.EcoreUtil

object ClassHelper {
    // Constants for class names
    final val CLASS : String = "Class"
    final val ATTRIBUTE : String = "Attribute"

    // Singleton for EPackage
    var pack : EPackage = null

    private def instantiatePackage: Unit ={
        if (pack == null)
            pack = EMFTool.loadEcore("class2relational/model/Class.ecore") // Load a resource
              .getContents.get(0) // Get the first (and unique) EObject of the resource
              .asInstanceOf[EPackage] // Convert it to a EPackage
    }

    /* *********************************************************************************** */

    // Return EClass of Table
    def getEClass_Class: EClass = {
        instantiatePackage
        pack.getEClassifier(CLASS).asInstanceOf[EClass]
    }

    def buildClass(id: String, name: String, attributes: EObject = null): EObject = {
        val eclass = getEClass_Class
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val attributes_ref : EReference = eclass.getEAllReferences.get(0)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p.eSet(attributes_ref, attributes)
        p
    }

    def getClass_Id(c: EObject): String = {
        val eclass = getEClass_Class
        val id_field = eclass.getEAllAttributes.get(0)
        c.eGet(id_field).asInstanceOf[String]
    }

    def getClass_name(c: EObject): String = {
        val eclass = getEClass_Class
        val name_field = eclass.getEAllAttributes.get(1)
        c.eGet(name_field).asInstanceOf[String]
    }

    def getClass_attributes(c: EObject): EObject = {
        val eclass = getEClass_Class
        val attributes_field = eclass.getEAllReferences.get(0)
        c.eGet(attributes_field).asInstanceOf[EObject]
    }

    /* *********************************************************************************** */

    // Return EClass of Column
    def getEClass_Attribute: EClass = {
        instantiatePackage
        pack.getEClassifier(ATTRIBUTE).asInstanceOf[EClass]
    }

    def buildAttribute(id: String, name: String, table: EObject = null): EObject = {
        assert(table.eClass() == getEClass_Class)
        val eclass = getEClass_Attribute
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val table_ref : EReference = eclass.getEAllReferences.get(0)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p.eSet(table_ref, table)
        p
    }

    def getAttribute_Id(c: EObject): String = {
        val eclass = getEClass_Attribute
        val id_field = eclass.getEAllAttributes.get(0)
        c.eGet(id_field).asInstanceOf[String]
    }

    def getAttribute_name(c: EObject): String = {
        val eclass = getEClass_Attribute
        val name_field = eclass.getEAllAttributes.get(1)
        c.eGet(name_field).asInstanceOf[String]
    }

    def getAttribute_reference(c: EObject): EObject = {
        val eclass = getEClass_Attribute
        val ref_field = eclass.getEAllAttributes.get(2)
        c.eGet(ref_field).asInstanceOf[EObject]
    }

    def getAttribute_isDerived(c: EObject): Boolean = {
        // TODO
        val eclass = getEClass_Attribute
        eclass.getEAllAttributes.get(2).isDerived
    }
}
