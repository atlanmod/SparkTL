package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.dblpinfo.model.authorinfo.{AuthorInfoElement, AuthorInfoMetamodel}

class AuthorInfoAuthor extends AuthorInfoElement(AuthorInfoMetamodel.AUTHOR){

    def this(name: String, numOfPapers: Int = 0, active: Boolean = false) = {
        this()
        super.eSetProperty("name", name)
        super.eSetProperty("numOfPapers", numOfPapers)
        super.eSetProperty("active", active)
    }

    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
