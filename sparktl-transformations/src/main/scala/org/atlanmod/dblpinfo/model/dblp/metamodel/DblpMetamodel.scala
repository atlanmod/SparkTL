package org.atlanmod.dblpinfo.model.dblp.metamodel

import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpJournal, DblpRecord}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait DblpMetamodel extends Serializable {

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

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("DBLPMetamodel")

    def getRecordsOfAuthor(model: DblpModel, author: DblpAuthor): Option[List[DblpRecord]]

    def getAuthorsOfRecord(m: DblpModel, r: DblpRecord): Option[List[DblpAuthor]]

    def getJournalOfArticle(model: DblpModel, a: DblpArticle): Option[DblpJournal]
}