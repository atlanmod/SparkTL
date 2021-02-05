package org.atlanmod.model.classmodel

import org.atlanmod.model.DynamicElement
import scala.collection.mutable

abstract class ClassElement(classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    def getId: String

    override def getType: String = classname

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ClassElement =>
                this.getType().equals(obj.getType()) && this.getId().equals(obj.getId())
            case _ => false
        }
    }

}
