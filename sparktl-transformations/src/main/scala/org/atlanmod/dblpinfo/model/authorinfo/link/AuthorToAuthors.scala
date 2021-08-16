package org.atlanmod.dblpinfo.model.authorinfo.link

import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoAuthor
import org.atlanmod.dblpinfo.model.authorinfo.{AuthorInfoLink, AuthorInfoMetamodel}

class AuthorToAuthors (source: AuthorInfoAuthor, target: List[AuthorInfoAuthor])
  extends AuthorInfoLink(AuthorInfoMetamodel.COAUTHOR, source, target) {

    def this(source: AuthorInfoAuthor, target: AuthorInfoAuthor) =
        this(source, List(target))

    override def getSource: AuthorInfoAuthor = source
    override def getTarget: List[AuthorInfoAuthor] = target

    override def toString: String = source.toString + " 's coauthors: " + target.mkString(",", "[", "]")

}
