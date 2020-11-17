package org.atlanmod

import org.atlanmod.tl.model.Metamodel
import org.eclipse.emf.ecore.{EClass, EObject, EReference}

class EMFMetamodel extends Metamodel[EObject, ELink, EClass, EReference] {

    def toModelClass(sc: EClass, se: EObject): Option[EObject] = {
        if (se.eClass().getEAllSuperTypes().contains(sc))
            Some (se)
        else
            None
    }

    def toModelReference(sr: EReference, sl: ELink): Option[ELink] =
        if (sl.getReference.equals(sr))
            Some(sl)
        else  None

    override def equals(that: Any): Boolean = {
        that match {
            case _ : Metamodel[EObject, ELink, EClass, EReference] => true
            case _ => false
        }
    }

}
