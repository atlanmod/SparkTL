package org.atlanmod.dblpinfo.model.authorinfo

import org.atlanmod.tl.model.impl.dynamic.DynamicElement

import scala.collection.mutable

abstract class AuthorInfoElement (classname: String) extends DynamicElement(classname, mutable.HashMap()) {

    override def equals(o: Any): Boolean = {
        o match {
            case obj: AuthorInfoElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: AuthorInfoElement =>
                this.getType.equals(obj.getType)
            case _ => false
        }
    }

}
