package org.atlanmod.tl.model.impl.emf.serializable.string

import org.eclipse.emf.ecore.{EClass, EObject, EStructuralFeature}

class SerializableEObject (object_ : EObject) extends Serializable {
    def eObject() : EObject = object_
    def eClass() : EClass = object_.eClass()
    def eGet(feature: SerializableEStructuralFeature): AnyRef = object_.eGet(feature.eStructuralFeature())
    def eGet(feature: EStructuralFeature): AnyRef = object_.eGet(feature)
}
