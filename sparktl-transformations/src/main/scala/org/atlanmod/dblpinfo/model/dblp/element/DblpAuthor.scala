package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.dblp.DblpElement
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class DblpAuthor extends DblpElement(DblpMetamodelNaive.AUTHOR) {

    def this(id: Long, name: String){
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    def this(name: String){
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpAuthor =>
                this.getName.equals(obj.getName)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}
