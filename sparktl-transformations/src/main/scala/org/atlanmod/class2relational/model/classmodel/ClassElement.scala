package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicElement

import scala.collection.mutable

abstract class ClassElement(classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    def getId: String

    override def getType: String = classname

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ClassElement =>
                this.getType.equals(obj.getType) && this.getId.equals(obj.getId)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }

}
