package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EPackage

class EPackageWrapper (p: EPackage) extends WrapperSerializable[EPackage] {
    def unwrap: EPackage = p
}