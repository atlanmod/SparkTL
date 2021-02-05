package org.atlanmod.model

import scala.collection.mutable

class DynamicElement(classname: String,
                     properties : scala.collection.mutable.Map[String, Any] = mutable.HashMap())
  extends Serializable {

//    def this(element: DynamicElement, properties: scala.collection.mutable.Map[String, Any] = null) {
//        this(element.getType, if (properties == null) element.getProperties else properties)
//    }

    def getType: String = { classname }

    def getProperties: scala.collection.mutable.Map[String, Any] = { this.properties }

    def eGetProperty(name: String): Any = {
        this.properties.get(name) match {
            case Some(v) => v
            case _ => null
        }
    }

    def eSetProperty(name: String, value: Any) : Unit = {
        if (!properties.isDefinedAt(name)) properties.put(name, value)
    }

    override def equals(o : Any): Boolean = {
        o match {
            case obj: DynamicElement =>
                this.classname.equals(obj.getType) &&
                  this.properties.equals(obj.getProperties)
            case _ => false
        }
    }

    override def toString: String = {
        var res = ""
        res += classname + "\n"
        if (properties.nonEmpty) { properties.mkString("\n") }
        res
    }

}
