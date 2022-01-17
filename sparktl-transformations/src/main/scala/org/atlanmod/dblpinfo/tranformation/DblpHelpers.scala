package org.atlanmod.dblpinfo.tranformation

import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpInProceedings, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodel

object DblpHelpers {

    def helper_booktitle(model: DblpModel, ip: DblpInProceedings) : String = ip.getBookTitle
    def helper_year(model: DblpModel, ip: DblpInProceedings) : Int = ip.getYear
    def helper_year(model: DblpModel, ip: DblpArticle) : Int = ip.getYear

    def helper_getAuthors(m: DblpModel, ip: DblpRecord, meta: DblpMetamodel): List[DblpAuthor] =
        meta.getAuthorsOfRecord(m, ip) match {
            case Some(res) => res
            case None => List()
        }

    def helper_journal(model: DblpModel, article: DblpArticle, meta: DblpMetamodel): String =
        meta.getJournalOfArticle(model, article) match {
            case Some(journal) => journal.getName
            case _ => ""
        }

    def helper_numOfPapers(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel) : Int =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .count(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0)
            case _ => 0
        }

    def helper_hasPapersICMT(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel) : Boolean =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0)
            case _ => false
        }

    def helper_active_ICMT(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel) : Boolean =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0 && helper_year(model, ip) > 2008)
            case _ => false
        }

    def helper_active_IST(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel) : Boolean =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpArticle])
                  .map(r => r.asInstanceOf[DblpArticle])
                  .exists(article =>
                      helper_journal(model, article, meta).indexOf("Information & Software Technology") >= 0 &&
                        helper_year(model, article) > 2005)
            case _ => false
        }

    def helper_ICMT_nowPublishingIn(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel) : List[DblpInProceedings] =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) => records.filter(r => r.isInstanceOf[DblpInProceedings])
              .map(r => r.asInstanceOf[DblpInProceedings])
              .filter(ip => helper_booktitle(model, ip).indexOf("ICMT") < 0 && helper_year(model, ip) > 2008)
            case _ => List()
        }

    def helper_hasPapersIST(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel): Boolean =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpArticle])
                  .map(r => r.asInstanceOf[DblpArticle])
                  .exists(article => helper_journal(model, article, meta).indexOf("Information & Software Technology") >= 0 )
            case _ => false
        }

    def helper_IST_nowPublishingIn(model: DblpModel, author: DblpAuthor, meta: DblpMetamodel): List[DblpArticle] =
        meta.getRecordsOfAuthor(model, author) match {
            case Some(records) => records.filter(r => r.isInstanceOf[DblpArticle])
              .map(r => r.asInstanceOf[DblpArticle])
              .filter(article => helper_journal(model, article, meta).indexOf("Information & Software Technology") < 0
                && helper_year(model, article) > 2005)
            case _ => List()
        }
}
