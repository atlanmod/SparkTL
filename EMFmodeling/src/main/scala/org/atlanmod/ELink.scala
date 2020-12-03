package org.atlanmod

import org.eclipse.emf.ecore.{EObject, EReference}

class ELink(src: EObject, ref: EReference, trg: Object) extends {

    def getSource: EObject = src
    def getReference: EReference = ref
    def getTarget: Object = trg

    override def toString: String = "(" + src.toString + ", " + ref.toString + ", " + trg.toString + ")"
}
