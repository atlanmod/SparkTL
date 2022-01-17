package org.atlanmod.dblpinfo.model.dblp.metamodel

import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpJournal, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.link.{AuthorToRecords, JournalToArticles, RecordToAuthors}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpModel}

object DblpMetamodelNaive extends DblpMetamodel {

    private def getRecordsOfAuthorOnLinks(links: List[DblpLink], author: DblpAuthor): Option[List[DblpRecord]] = {
        links.find(l => l.isInstanceOf[AuthorToRecords] && l.getSource.equals(author)) match {
            case Some(l: AuthorToRecords) => Some(l.getTarget)
            case _ => None
        }
    }

    def getRecordsOfAuthor(model: DblpModel, author: DblpAuthor): Option[List[DblpRecord]] =
        getRecordsOfAuthorOnLinks(model.allModelLinks, author)

    private def getAuthorsOfRecordOnLinks(links: List[DblpLink], record: DblpRecord): Option[List[DblpAuthor]] = {
        links.find(l => l.isInstanceOf[RecordToAuthors] && l.getSource.equals(record)) match {
            case Some(l: RecordToAuthors) => Some(l.getTarget)
            case _ => None
        }
    }

    def getAuthorsOfRecord(m: DblpModel, r: DblpRecord): Option[List[DblpAuthor]] =
        getAuthorsOfRecordOnLinks(m.allModelLinks, r)

    private def getJournalOfArticleOnLinks(links: List[DblpLink], a: DblpArticle): Option[DblpJournal] =
        links.find(l => l.isInstanceOf[JournalToArticles] && l.getTarget.contains(a)) match {
            case Some(l: JournalToArticles) => Some(l.getSource)
            case _ => None
        }

    def getJournalOfArticle(model: DblpModel, a: DblpArticle): Option[DblpJournal] =
        getJournalOfArticleOnLinks(model.allModelLinks, a)
}
