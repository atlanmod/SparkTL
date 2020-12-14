package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EObject

class EObjectWrapper (o: EObject) extends WrapperSerializable[EObject] {
    def unwrap: EObject = o
}
