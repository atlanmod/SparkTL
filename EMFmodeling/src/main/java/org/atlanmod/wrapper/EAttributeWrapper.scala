package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EAttribute

class EAttributeWrapper (e: EAttribute) extends Serializable {
    def getEAttribute: EAttribute = e
}
