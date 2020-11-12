package org.atlanmod.io

import org.eclipse.emf.ecore.{EObject, EReference}

class ELink(src: EObject, ref: EReference, trg: EObject){
    def getSource: EObject = src
    def getReference: EReference = ref
    def getTarget: EObject = trg
}
