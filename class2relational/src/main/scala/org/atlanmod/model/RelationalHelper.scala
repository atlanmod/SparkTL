package org.atlanmod.model

import org.atlanmod.tl.sequential.io.EMFTool
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.{EClass, EObject, EPackage, EReference}

object RelationalHelper {

    var pack : EPackage = null

    private def instantiatePackage(): Unit ={
        pack = EMFTool.loadEcore("class2relational/model/Relational.ecore") // Load a resource
          .getContents.get(0) // Get the first (and unique) EObject of the resource
          .asInstanceOf[EPackage] // Convert it to a EPackage
    }

    def buildTable(id: String, name: String): EObject = {
        if (pack == null) instantiatePackage
        val eclass = pack.getEClassifier("Table").asInstanceOf[EClass]
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p
    }

    def buildColumn(id: String, name: String, table: EObject): EObject = {
        if (pack == null) instantiatePackage
        val eclass = pack.getEClassifier("Column").asInstanceOf[EClass]
        val id_field = eclass.getEAllAttributes.get(0)
        val name_field = eclass.getEAllAttributes.get(1)
        val ref_reference : EReference = eclass.getEAllReferences.get(0)
        val p = EcoreUtil.create(eclass)
        p.eSet(id_field, id)
        p.eSet(name_field, name)
        p.eSet(ref_reference, table)
        p
    }

}
