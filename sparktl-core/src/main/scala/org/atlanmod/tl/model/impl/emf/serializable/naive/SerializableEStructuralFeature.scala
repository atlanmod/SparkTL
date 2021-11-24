package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.eclipse.emf.ecore.EStructuralFeature

class SerializableEStructuralFeature (feature: EStructuralFeature) extends Serializable {
    def eStructuralFeature() = feature
}
