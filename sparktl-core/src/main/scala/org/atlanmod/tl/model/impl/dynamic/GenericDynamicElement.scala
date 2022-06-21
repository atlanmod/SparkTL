package org.atlanmod.tl.model.impl.dynamic

import scala.collection.mutable

class GenericDynamicElement(classname: String, id: Long, properties : scala.collection.mutable.Map[String, Any] = mutable.HashMap())
  extends DynamicElement(classname, properties) {

    override def getId: Long = id

    override def weak_equals(o: Any): Boolean = {
        o match {
            case gde: GenericDynamicElement => classname.equals(gde.classname) &&
              id.equals(gde.id) && properties.equals(gde.properties)
            case _ => false
        }
    }
}
