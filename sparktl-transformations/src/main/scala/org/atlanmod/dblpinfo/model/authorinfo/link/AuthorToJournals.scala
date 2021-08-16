package org.atlanmod.dblpinfo.model.authorinfo.link

import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoJournal}
import org.atlanmod.dblpinfo.model.authorinfo.{AuthorInfoLink, AuthorInfoMetamodel}

class AuthorToJournals(source: AuthorInfoAuthor, target: List[AuthorInfoJournal])
  extends AuthorInfoLink(AuthorInfoMetamodel.PUBLISHESINJ, source, target) {

    def this(source: AuthorInfoAuthor, target: AuthorInfoJournal) =
        this(source, List(target))

    override def getSource: AuthorInfoAuthor = source
    override def getTarget: List[AuthorInfoJournal] = target

    override def toString: String = source.toString + " published in: " + target.mkString(",", "[", "]")

}
