package org.atlanmod.ttc18.model.socialnetwork

import org.atlanmod.tl.model.impl.dynamic.DynamicElement

import scala.collection.mutable

abstract class SocialNetworkElement(classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    override def getType: String = classname

    def getId: String

    override def equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkElement =>
                this.getType.equals(obj.getType) && this.getId.equals(obj.getId)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }
}