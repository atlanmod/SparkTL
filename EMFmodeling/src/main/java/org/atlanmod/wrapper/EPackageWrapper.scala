package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EPackage

class EPackageWrapper (p: EPackage) extends Serializable {
    def getEPackage: EPackage = p
}
