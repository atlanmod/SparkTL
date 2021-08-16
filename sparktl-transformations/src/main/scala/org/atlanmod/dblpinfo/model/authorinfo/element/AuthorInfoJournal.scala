package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.dblpinfo.model.authorinfo.{AuthorInfoElement, AuthorInfoMetamodel}

class AuthorInfoJournal extends AuthorInfoElement(AuthorInfoMetamodel.JOURNAL){

    def this(name: String) = {
        this()
        super.eSetProperty("name", name)
    }

    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
