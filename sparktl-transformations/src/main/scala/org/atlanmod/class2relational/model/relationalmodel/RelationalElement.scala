package org.atlanmod.class2relational.model.relationalmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicElement

import scala.collection.mutable

abstract class RelationalElement (classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    def getId: String

    override def getType: String = classname


    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: RelationalElement => this.getType.equals(obj.getType)
            case _ => false
        }
    }

    override def equals(o: Any): Boolean = {
        o match {
            case obj: RelationalElement => this.getType.equals(obj.getType) && this.getId.equals(obj.getId)
            case _ => false
        }
    }
}