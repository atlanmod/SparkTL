package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EReference

class EReferenceWrapper (r: EReference) extends Serializable {
    def getEReference: EReference = r
}
