package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EClass

class EClassWrapper (e: EClass) extends WrapperSerializable[EClass] {
    def unwrap : EClass = e
}
