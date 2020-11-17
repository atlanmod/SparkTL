package org.atlanmod.model

import org.atlanmod.tl.sequential.io.EMFTool
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.{EClass, EObject, EPackage, EReference}

object RelationalHelper {

    // Constants for class names
    final val TABLE : String = "Table"
    final val COLUMN : String = "Column"

    // Singleton for EPackage
    var pack : EPackage = null

    private def instantiatePackage: Unit ={
        if (pack == null)
            pack = EMFTool.loadEcore("class2relational/model/Relational.ecore") // Load a resource
              .getContents.get(0) // Get the first (and unique) EObject of the resource
              .asInstanceOf[EPackage] // Convert it to a EPackage
    }


    /* *********************************************************************************** */

    // Return EClass of Table
    def getEClass_Table: EClass = {
        instantiatePackage
        pack.getEClassifier(TABLE).asInstanceOf[EClass]
    }

    def buildTable(id: String, name: String, attributes: EObject = null): EObject = {
        val eclass = getEClass_Table
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val attributes_reference : EReference = eclass.getEAllReferences.get(0)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p.eSet(attributes_reference, attributes)
        p
    }

    def getTable_Id(c: EObject): String = {
        val eclass = getEClass_Table
        val id_field = eclass.getEAllAttributes.get(0)
        c.eGet(id_field).asInstanceOf[String]
    }

    def getTable_name(c: EObject): String = {
        val eclass = getEClass_Table
        val name_field = eclass.getEAllAttributes.get(1)
        c.eGet(name_field).asInstanceOf[String]
    }

    /* *********************************************************************************** */

    // Return EClass of Column
    def getEClass_Column: EClass = {
        instantiatePackage
        pack.getEClassifier(COLUMN).asInstanceOf[EClass]
    }

    def buildColumn(id: String, name: String, table: EObject = null): EObject = {
        assert(table.eClass() == getEClass_Table)
        val eclass = getEClass_Column
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val table_reference : EReference = eclass.getEAllReferences.get(0)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p.eSet(table_reference, table)
        p
    }

    def getColumn_Id(c: EObject): String = {
        val eclass = getEClass_Column
        val id_field = eclass.getEAllAttributes.get(0)
        c.eGet(id_field).asInstanceOf[String]
    }

    def getColumn_name(c: EObject): String = {
        val eclass = getEClass_Column
        val name_field = eclass.getEAllAttributes.get(1)
        c.eGet(name_field).asInstanceOf[String]
    }

    def getColumn_reference(c: EObject): EObject = {
        val eclass = getEClass_Column
        val ref_field = eclass.getEAllAttributes.get(2)
        c.eGet(ref_field).asInstanceOf[EObject]
    }

}
