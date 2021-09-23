package org.atlanmod.dblpinfo.model.dblp

import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpJournal, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.link.{AuthorToRecords, JournalToArticles, RecordToAuthors}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object DblpMetamodel {

    final val AUTHOR: String = "Author"
    final val RECORD: String = "Record"
    final val ARTICLE: String = "Article"
    final val BOOK: String = "Book"
    final val INCOLLECTION: String = "InCollection"
    final val INPROCEEDINGS: String = "InProceedings"
    final val PROCEEDINGS: String = "Proceedings"
    final val MASTERSTHESIS: String = "MastersThesis"
    final val PHDTHESIS: String = "PhdThesis"
    final val WWW: String = "Www"
    final val SCHOOL: String = "School"
    final val JOURNAL: String = "Journal"
    final val ORGANIZATION: String = "Organization"
    final val PUBLISHER: String = "Publisher"
    final val EDITOR: String = "Editor"

    final val AUTHOR_RECORDS: String = "records"
    final val RECORD_AUTHORS: String = "authors"
    final val ARTICLE_JOURNAL: String = "journal"
    final val JOURNAL_ARTICLES: String = "articles"
    final val BOOK_PUBLISHER: String = "publisher"
    final val INCOLLECTION_PUBLISHER: String = "publisher"
    final val INCOLLECTION_EDITORS: String = "editors"
    final val INCOLLECTION_ORGANIZATION: String = "sponsoredBy"
    final val INPROCEEDINGS_ORGANIZATION: String = "organization"
    final val INPROCEEDINGS_PUBLISHER: String = "publisher"
    final val INPROCEEDINGS_EDITORS: String = "editors"
    final val PROCEEDINGS_ORGANIZATION: String = "sponsoredBy"
    final val PROCEEDINGS_PUBLISHER: String = "publisher"
    final val PROCEEDINGS_EDITORS: String = "editors"
    final val MASTERSTHESIS_SCHOOL: String = "school"
    final val PHDTHESIS_SCHOOL: String = "school"
    final val WWW_EDITORS: String = "editors"

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]()

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
