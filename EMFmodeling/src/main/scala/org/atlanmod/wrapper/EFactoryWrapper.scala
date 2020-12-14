package org.atlanmod.wrapper

import org.eclipse.emf.ecore.EFactory

class EFactoryWrapper(f: EFactory) extends WrapperSerializable[EFactory] {
    def unwrap : EFactory = f
}
