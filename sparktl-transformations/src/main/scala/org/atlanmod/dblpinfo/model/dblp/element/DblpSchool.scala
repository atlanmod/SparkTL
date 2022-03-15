package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.dblp.DblpElement
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class DblpSchool extends DblpElement(DblpMetamodelNaive.SCHOOL) {

    def this(id: Long, name: String, address: String){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("address", address)
    }

    def this(name: String, address: String){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("address", address)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
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