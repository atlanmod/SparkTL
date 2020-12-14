package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EObject

class EObjectWrapper (o: EObject) extends Serializable {
    def getEObject: EObject = o
}
