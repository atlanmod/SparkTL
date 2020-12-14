package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EAttribute

class EAttributeWrapper (e: EAttribute) extends WrapperSerializable[EAttribute] {
    def unwrap: EAttribute = e
}
