package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EClass

class EClassWrapper (e: EClass) extends Serializable {
    def getEClass : EClass = e
}
