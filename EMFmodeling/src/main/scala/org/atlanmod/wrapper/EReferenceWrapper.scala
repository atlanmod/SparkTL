package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EReference

class EReferenceWrapper (r: EReference) extends WrapperSerializable[EReference] {
    def unwrap: EReference = r
}
