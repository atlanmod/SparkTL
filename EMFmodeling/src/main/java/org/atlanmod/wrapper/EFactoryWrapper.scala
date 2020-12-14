package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EFactory

class EFactoryWrapper(f: EFactory) extends Serializable {
    def getEFactory : EFactory = f
}
