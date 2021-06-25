package org.atlanmod.tl.model.impl.dynamic

import org.atlanmod.tl.util.ListUtils

abstract class DynamicLink(type_ : String, source: DynamicElement, target: List[DynamicElement])
  extends Serializable with ListUtils.Weakable {

    def this(type_ : String, source: DynamicElement, target: DynamicElement) {
        this(type_, source, List(target))
    }

    def getType: String = { this.type_ }

    def getSource: DynamicElement = { this.source }

    def getTarget: List[DynamicElement] = { this.target }

    override def toString: String = {
        source.toString + "\n" + type_ + "\n" + target.toString + "\n"
    }

    override def weak_equals(o: Any): Boolean

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DynamicLink =>
                this.getType.equals(obj.getType) &&
                  this.getSource.equals(obj.getSource) &&
                  this.getTarget.equals(obj.getTarget)
            case _ => false
        }
    }
}
