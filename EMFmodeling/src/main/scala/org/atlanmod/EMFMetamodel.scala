package org.atlanmod

import org.atlanmod.tl.model.Metamodel
import org.eclipse.emf.ecore.{EClass, EObject, EReference}

class EMFMetamodel extends Metamodel[EObject, ELink, EClass, EReference] {

    def toModelClass(sc: EClass, se: EObject): Option[EClass] =
        Some(sc).filter(se.eClass().getEAllSuperTypes().contains)

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

//    override def denoteClass(sc: EClass): EClass = sc
}
