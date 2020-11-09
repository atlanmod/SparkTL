package org.atlanmod.tl.model

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference

trait Metamodel[ME, ML, MC, MR] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    *  MC: ModelClass
    *  MR: ModelReference
    */

    def toModelClass(sc: MC, se: ME): Option[ME]
    def toModelReference(sr: MR, sl: ML): Option[ML]

    def equals(that: Any): Boolean

}

class ELink(src: EObject, ref: EReference, trg: EObject){
    def getSource: EObject = src
    def getReference: EReference = ref
    def getTarget: EObject = trg
}

class EMFMetamodel extends Metamodel[EObject, ELink, EClass, EReference ] {
    def toModelClass(sc: EClass, se: EObject): Option[EObject] = {
        if (se.eClass().getEAllSuperTypes().contains(sc)) 
            Some (se)
        else 
            None
    }

    def toModelReference(sr: EReference, sl: ELink): Option[Object] = {
        if (sl.getReference.equals(sr)) 
            Some (sr)
        else 
            None
    }
}
