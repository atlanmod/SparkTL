package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpJournal}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class ArticleToJournal(source: DblpArticle, target: DblpJournal)
  extends DblpLink(DblpMetamodel.ARTICLE_JOURNAL, source, List(target)) {

    override def getSource: DblpArticle = source
    override def getTarget: List[DblpJournal] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}