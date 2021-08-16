package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpJournal}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class JournalToArticles(source: DblpJournal, target: List[DblpArticle])
  extends DblpLink(DblpMetamodel.JOURNAL_ARTICLES, source, target) {

    def this(source: DblpJournal, target: DblpArticle) =
        this(source, List(target))


    override def getSource: DblpJournal = source
    override def getTarget: List[DblpArticle] = target

    override def toString: String = source.toString + "'s publications: \n" + target.mkString("","\n -","")

}