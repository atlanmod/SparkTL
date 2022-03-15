package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoElement
import org.atlanmod.dblpinfo.model.authorinfo.metamodel.AuthorInfoMetamodelNaive

class AuthorInfoJournal extends AuthorInfoElement(AuthorInfoMetamodelNaive.JOURNAL){

    def this(name: String) = {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    def this(id: Long, name: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
