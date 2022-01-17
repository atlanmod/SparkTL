package org.atlanmod.dblpinfo.model.authorinfo.link

import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoLink
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoConference}
import org.atlanmod.dblpinfo.model.authorinfo.metamodel.AuthorInfoMetamodelNaive

class AuthorToConferences (source: AuthorInfoAuthor, target: List[AuthorInfoConference])
  extends AuthorInfoLink(AuthorInfoMetamodelNaive.PUBLISHESINC, source, target) {

    def this(source: AuthorInfoAuthor, target: AuthorInfoConference) =
        this(source, List(target))

    override def getSource: AuthorInfoAuthor = source
    override def getTarget: List[AuthorInfoConference] = target

    override def toString: String = source.toString + " published in: " + target.mkString(",", "[", "]")

}
