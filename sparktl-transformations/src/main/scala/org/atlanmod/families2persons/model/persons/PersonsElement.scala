package org.atlanmod.families2persons.model.persons

import org.atlanmod.tl.model.impl.dynamic.DynamicElement

import scala.collection.mutable

abstract class PersonsElement(classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    override def equals(o: Any): Boolean = {
        o match {
            case obj: PersonsElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: PersonsElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }

}
