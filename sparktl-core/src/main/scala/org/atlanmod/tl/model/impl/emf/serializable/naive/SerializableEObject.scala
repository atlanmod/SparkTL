package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.eclipse.emf.ecore.{EObject, EStructuralFeature}

class SerializableEObject (object_ : EObject) extends Serializable {
    def eObject() : EObject = object_
    def eClass() : SerializableEClass = new SerializableEClass(object_.eClass())
    def eGet(feature: SerializableEStructuralFeature): AnyRef = object_.eGet(feature.eStructuralFeature())
    def eGet(feature: EStructuralFeature): AnyRef = object_.eGet(feature)
}
