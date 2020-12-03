package org.atlanmod

import org.eclipse.emf.ecore.{EObject, EReference}

class ELink(src: EObject, ref: EReference, trg: Object){
    // TODO : add a trait in transformation.model : Link + with 3 getters
    def getSource: EObject = src
    def getReference: EReference = ref
    def getTarget: Object = trg

    override def toString: String = "(" + src.toString + ", " + ref.toString + ", " + trg.toString + ")"
}
