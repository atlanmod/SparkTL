package org.atlanmod.dblpinfo.model.authorinfo.element

import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoElement
import org.atlanmod.dblpinfo.model.authorinfo.metamodel.AuthorInfoMetamodelNaive

class AuthorInfoConference  extends AuthorInfoElement(AuthorInfoMetamodelNaive.CONFERENCE){

    def this(name: String) = {
        this()
        super.eSetProperty("name", name)
    }

    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}
