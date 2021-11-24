package org.atlanmod.tl.model.impl.emf.serializable.string

import org.atlanmod.tl.util.ListUtils

class SerializableELink(type_ : SerializableEStructuralFeature, source: SerializableEObject, target: List[SerializableEObject]) extends Serializable
  with ListUtils.Weakable {

    def this(type_ : SerializableEStructuralFeature, source: SerializableEObject, target: SerializableEObject) {
        this(type_, source, List(target))
    }

    def getType: SerializableEStructuralFeature = { this.type_ }

    def getSource: SerializableEObject = { this.source }

    def getTarget: List[SerializableEObject] = { this.target }

    override def toString: String = {
        source.toString + "\n" + type_ + "\n" + target.toString + "\n"
    }

    override def weak_equals(o: Any): Boolean = this.equals(o)

    override def equals(o: Any): Boolean = {
        o match {
            case obj: SerializableELink =>
                this.getType.equals(obj.getType) &&
                  this.getSource.equals(obj.getSource) &&
                  this.getTarget.equals(obj.getTarget)
            case _ => false
        }
    }
}