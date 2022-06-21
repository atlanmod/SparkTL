package org.atlanmod.tl.model.impl.dynamic

import org.atlanmod.tl.util.ListUtils

abstract class DynamicLinkById(type_ : String, source: Long, target: List[Long])
  extends Serializable with ListUtils.Weakable {

    def this(type_ : String, source: Long, target: Long) {
        this(type_, source, List(target))
    }

    def getType: String = { this.type_ }

    def getSource: Long = { this.source }

    def getTarget: List[Long] = { this.target }

    override def toString: String = {
        source.toString + "\n" + type_ + "\n" + target.toString + "\n"
    }

    override def weak_equals(o: Any): Boolean

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DynamicLinkById =>
                this.getType.equals(obj.getType) &&
                  this.getSource.equals(obj.getSource) &&
                  this.getTarget.equals(obj.getTarget)
            case _ => false
        }
    }

    override def hashCode(): Int = {
        source.hashCode() + target.hashCode() + type_.hashCode()
    }

}
