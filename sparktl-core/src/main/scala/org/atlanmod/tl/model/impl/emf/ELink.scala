package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.util.ListUtils
import org.eclipse.emf.ecore.{EObject, EStructuralFeature}

class ELink(type_ : EStructuralFeature, source: EObject, target: List[EObject]) extends Serializable
  with ListUtils.Weakable {

    def this(type_ : EStructuralFeature, source: EObject, target: EObject) {
        this(type_, source, List(target))
    }

    def getType: EStructuralFeature = { this.type_ }

    def getSource: EObject = { this.source }

    def getTarget: List[EObject] = { this.target }

    override def toString: String = {
        source.toString + "\n" + type_ + "\n" + target.toString + "\n"
    }

    override def weak_equals(o: Any): Boolean = this.equals(o)

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ELink =>
                this.getType.equals(obj.getType) &&
                  this.getSource.equals(obj.getSource) &&
                  this.getTarget.equals(obj.getTarget)
            case _ => false
        }
    }
}