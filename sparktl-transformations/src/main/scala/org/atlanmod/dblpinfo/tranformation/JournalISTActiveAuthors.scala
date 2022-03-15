package org.atlanmod.dblpinfo.tranformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoJournal}
import org.atlanmod.dblpinfo.model.authorinfo.link.AuthorToJournals
import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.{DblpArticle, DblpAuthor}
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodel
import org.atlanmod.dblpinfo.tranformation.DblpHelpers._
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}

import scala.collection.mutable
import scala.util.Random

object JournalISTActiveAuthors {

    final val PATTERN_AUTHOR_IST = "author_IST"
    final val PATTERN_JOURNAL_IST = "journal_IST"

    val random: Random.type = scala.util.Random

    var journals: mutable.HashMap[String, AuthorInfoJournal] = new mutable.HashMap[String, AuthorInfoJournal]()

    def makeAuthorToJournal(meta: DblpMetamodel, tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
        val articles = helper_IST_nowPublishingIn(model, input_author, meta)
        var jours: List[AuthorInfoJournal] = List()
        for(article <- articles){
            journals.get(helper_journal(model, article, meta)) match {
                case Some(journal) => jours = journal :: jours
                case _ =>
            }
        }
        Some(new AuthorToJournals(authorinfo, jours))
    }

    def find(meta: DblpMetamodel,sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(meta.AUTHOR),
                from = (m, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val author = pattern.head.asInstanceOf[DblpAuthor]
                    val model = m.asInstanceOf[DblpModel]
                    Some (
                        helper_hasPapersIST(model, author, meta) & helper_active_IST(model, author, meta)
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_AUTHOR_IST,
                        elementExpr = (_, model, pattern) =>
                          if (pattern.isEmpty) None else {
                              my_sleep(sleeping_instantiate, random.nextInt)
                              val author = pattern.head.asInstanceOf[DblpAuthor]
                              val active = helper_active_IST(model.asInstanceOf[DblpModel], author, meta)
                              Some(new AuthorInfoAuthor(author.getName, 0, active = active))
                          },
                        outputElemRefs = List (
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
                                    my_sleep(sleeping_apply, random.nextInt)
                                    val author = pattern.head.asInstanceOf[DblpAuthor]
                                    val model = sm.asInstanceOf[DblpModel]
                                    val authorinfo = output.asInstanceOf[AuthorInfoAuthor]
                                    makeAuthorToJournal(meta, tls, model, author, authorinfo)
                                }
                            )

                        )
                    )
                )
            ),
            new RuleImpl(
                name = "jour",
                types = List(meta.ARTICLE),
                from = (m, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val article = pattern.head.asInstanceOf[DblpArticle]
                    val model = m.asInstanceOf[DblpModel]
                    Some(
                     helper_journal(model, article, meta).indexOf("Information & Software Technology") < 0
                       & helper_getAuthors(model, article, meta).exists(a => helper_active_IST(model, a, meta)) &
                       !journals.isDefinedAt(helper_journal(model, article, meta))
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_JOURNAL_IST,
                        elementExpr = (_, model, pattern) => {
                            my_sleep(sleeping_instantiate, random.nextInt)
                            val article = pattern.head.asInstanceOf[DblpArticle]
                            val res = new AuthorInfoJournal(helper_journal(model.asInstanceOf[DblpModel], article, meta))
                            journals.put(helper_journal(model.asInstanceOf[DblpModel], article, meta), res)
                            Some(res)
                        }
                    )
                )
            )
        ))
    }
}