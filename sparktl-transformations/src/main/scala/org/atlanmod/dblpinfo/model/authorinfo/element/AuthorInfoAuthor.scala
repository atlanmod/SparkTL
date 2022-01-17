package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoElement
import org.atlanmod.dblpinfo.model.authorinfo.metamodel.AuthorInfoMetamodelNaive

class AuthorInfoAuthor extends AuthorInfoElement(AuthorInfoMetamodelNaive.AUTHOR){

    def this(name: String, numOfPapers: Int = 0, active: Boolean = false) = {
        this()
        super.eSetProperty("name", name)
        super.eSetProperty("numOfPapers", numOfPapers)
        super.eSetProperty("active", active)
    }

    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
