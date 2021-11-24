package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.eclipse.emf.ecore.EReference

class SerializableEReference (reference : EReference) extends SerializableEStructuralFeature (reference) {
    def eReference() : EReference = reference
}
