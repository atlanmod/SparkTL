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

}