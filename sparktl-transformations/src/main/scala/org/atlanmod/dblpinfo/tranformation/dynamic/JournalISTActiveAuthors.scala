package org.atlanmod.dblpinfo.tranformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoMetamodel
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoConference, AuthorInfoJournal}
import org.atlanmod.dblpinfo.model.authorinfo.link.AuthorToJournals
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

import scala.collection.mutable

object JournalISTActiveAuthors {

    final val PATTERN_AUTHOR_IST = "author_IST"
    final val PATTERN_JOURNAL_IST = "journal_IST"

    val mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def journals: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

    def helper_year(model: DblpModel, ip: DblpArticle) : Int = ip.getYear

    def helper_active(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author)
          .filter(r => r.isInstanceOf[DblpArticle])
          .map(r => r.asInstanceOf[DblpArticle])
          .exists(article => helper_journal(model, article).indexOf("Information & Software Technology") >= 0 && helper_year(model, article) > 2005)

    def helper_journal(model: DblpModel, article: DblpArticle): String =
        DblpMetamodel.getJournalOfArticle(model, article) match {
            case Some(journal) => journal.getName
            case _ => ""
        }

    def helper_getAuthors(m: DblpModel, ip: DblpRecord): List[DblpAuthor] =
        DblpMetamodel.getAuthorsOfRecord(m, ip) match {
            case Some(res) => res
            case None => List()
        }

    def helper_nowPublishingIn(model: DblpModel, author: DblpAuthor): List[DblpArticle] =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) => records.filter(r => r.isInstanceOf[DblpArticle])
              .map(r => r.asInstanceOf[DblpArticle])
              .filter(article => helper_journal(model, article).indexOf("Information & Software Technology") < 0 && helper_year(model, article) > 2005)
            case _ => List()
        }

    def makeAuthorToJournal(tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
        val articles = helper_nowPublishingIn(model, input_author)
        Resolve.resolveAll(tls, model, mm, PATTERN_JOURNAL_IST, AuthorInfoMetamodel.JOURNAL, ListUtils.singletons(articles))
        match {
            case Some(journals: List[AuthorInfoJournal]) => Some(new AuthorToJournals(authorinfo, journals))
            case _ => None
        }
    }

    def find: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(DblpMetamodel.AUTHOR),
                from = (m, pattern) => {
                    val author = pattern.head.asInstanceOf[DblpAuthor]
                    val model = m.asInstanceOf[DblpModel]
                    Some (
                        DblpMetamodel.getRecordsOfAuthor(model, author)
                          .filter(r => r.isInstanceOf[DblpArticle]).map(r => r.asInstanceOf[DblpArticle])
                          .exists(a => helper_journal(model, a).indexOf("Information & Software Technology") >= 0) &
                          helper_active(model, author)
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_AUTHOR_IST,
                        elementExpr = (_, model, pattern) =>
                          if (pattern.isEmpty) None else {
                              val author = pattern.head.asInstanceOf[DblpAuthor]
                              val active = helper_active(model.asInstanceOf[DblpModel], author)
                              Some(new AuthorInfoAuthor(author.getName, active = active))
                          },
                        outputElemRefs = List (
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
                                    val author = pattern.head.asInstanceOf[DblpAuthor]
                                    val model = sm.asInstanceOf[DblpModel]
                                    val authorinfo = output.asInstanceOf[AuthorInfoAuthor]
                                    makeAuthorToJournal(tls, model, author, authorinfo)
                                }
                            )

                        )
                    )
                )
            ),
            new RuleImpl(
                name = "jour",
                types = List(DblpMetamodel.ARTICLE),
                from = (m, pattern) => {
                    val article = pattern.head.asInstanceOf[DblpArticle]
                    val model = m.asInstanceOf[DblpModel]
                    Some(
                     helper_journal(model, article).indexOf("Information & Software Technology") < 0
                       & helper_getAuthors(model, article).exists(a => helper_active(model, a))
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_JOURNAL_IST,
                        elementExpr = (_, model, pattern) => {
                            val article = pattern.head.asInstanceOf[DblpArticle]
                            Some(new AuthorInfoJournal(helper_journal(model.asInstanceOf[DblpModel], article)))
                        }
                    )
                )
            )
        ))
}