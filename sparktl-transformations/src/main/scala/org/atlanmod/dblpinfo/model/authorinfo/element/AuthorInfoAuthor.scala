package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.IdGenerator
import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoElement
import org.atlanmod.dblpinfo.model.authorinfo.metamodel.AuthorInfoMetamodelNaive

class AuthorInfoAuthor extends AuthorInfoElement(AuthorInfoMetamodelNaive.AUTHOR){

    def this(id: Long, name: String, numOfPapers: Int, active: Boolean) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("numOfPapers", numOfPapers)
        super.eSetProperty("active", active)
    }

    def this(name: String, numOfPapers: Int, active: Boolean) = {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
        super.eSetProperty("numOfPapers", numOfPapers)
        super.eSetProperty("active", active)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
