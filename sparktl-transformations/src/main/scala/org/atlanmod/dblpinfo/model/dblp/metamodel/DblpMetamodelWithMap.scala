package org.atlanmod.dblpinfo.model.dblp.metamodel

import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpJournal, DblpRecord}

object DblpMetamodelWithMap extends DblpMetamodel {

    def getRecordsOfAuthor(model: DblpModel, author: DblpAuthor): Option[List[DblpRecord]] =
        metamodel.allLinksOfTypeOfElement(author, AUTHOR_RECORDS,model) match {
            case Some(e: List[DblpRecord]) => Some(e)
            case _ => None
        }

    def getAuthorsOfRecord(model: DblpModel, rec: DblpRecord): Option[List[DblpAuthor]] =
        metamodel.allLinksOfTypeOfElement(rec, RECORD_AUTHORS, model) match {
            case Some(e: List[DblpAuthor]) => Some(e)
            case _ => None
        }

    def getJournalOfArticle(model: DblpModel, art: DblpArticle): Option[DblpJournal] =
        metamodel.allLinksOfTypeOfElement(art, ARTICLE_JOURNAL, model) match {
            case Some(e: List[DblpJournal]) => Some(e.head)
            case _ => None
        }
}