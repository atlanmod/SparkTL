package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.{DblpElement, DblpMetamodel}

class DblpSchool extends DblpElement(DblpMetamodel.SCHOOL) {

    def this(name: String, address: String){
        this()
        super.eSetProperty("name", name)
        super.eSetProperty("address", address)
    }

    def getName: String = super.eGetProperty("name").asInstanceOf[String]
    def getAddress: String = super.eGetProperty("address").asInstanceOf[String]

    override def toString: String = getName + "(" + getAddress + ")"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpSchool =>
                super.equals(o) & obj.getName.equals(getName)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}