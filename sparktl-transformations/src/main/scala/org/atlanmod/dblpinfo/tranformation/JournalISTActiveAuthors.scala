package org.atlanmod.dblpinfo.tranformation

object JournalISTActiveAuthors {

//    final val PATTERN_AUTHOR_IST = "author_IST"
//    final val PATTERN_JOURNAL_IST = "journal_IST"
//
//    val random = scala.util.Random
//    val mm = DblpMetamodel.metamodel
//
//    val journals: mutable.HashMap[String, AuthorInfoJournal] = new mutable.HashMap[String, AuthorInfoJournal]()
//
//    def helper_year(model: DblpModel, ip: DblpArticle) : Int = ip.getYear
//
//    def helper_active(model: DblpModel, author: DblpAuthor) : Boolean =
//        DblpMetamodel.getRecordsOfAuthor(model, author) match {
//            case Some(records) =>
//                records.filter(r => r.isInstanceOf[DblpArticle])
//                  .map(r => r.asInstanceOf[DblpArticle])
//                  .exists(article =>
//                      helper_journal(model, article).indexOf("Information & Software Technology") >= 0 &&
//                        helper_year(model, article) > 2005)
//            case _ => false
//        }
//
//    def helper_hasPapersIST(model: DblpModel, author: DblpAuthor) =
//        DblpMetamodel.getRecordsOfAuthor(model, author) match {
//        case Some(records) =>
//            records.filter(r => r.isInstanceOf[DblpArticle])
//              .map(r => r.asInstanceOf[DblpArticle])
//              .exists(article => helper_journal(model, article).indexOf("Information & Software Technology") >= 0 )
//        case _ => false
//    }
//
//    def helper_journal(model: DblpModel, article: DblpArticle): String =
//        DblpMetamodel.getJournalOfArticle(model, article) match {
//            case Some(journal) => journal.getName
//            case _ => ""
//        }
//
//    def helper_getAuthors(m: DblpModel, ip: DblpRecord): List[DblpAuthor] =
//        DblpMetamodel.getAuthorsOfRecord(m, ip) match {
//            case Some(res) => res
//            case None => List()
//        }
//
//    def helper_nowPublishingIn(model: DblpModel, author: DblpAuthor): List[DblpArticle] =
//        DblpMetamodel.getRecordsOfAuthor(model, author) match {
//            case Some(records) => records.filter(r => r.isInstanceOf[DblpArticle])
//              .map(r => r.asInstanceOf[DblpArticle])
//              .filter(article => helper_journal(model, article).indexOf("Information & Software Technology") < 0
//                && helper_year(model, article) > 2005)
//            case _ => List()
//        }
//
//    def makeAuthorToJournal(tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
//                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
//        val articles = helper_nowPublishingIn(model, input_author)
//        var jours: List[AuthorInfoJournal] = List()
//        for(article <- articles){
//            journals.get(helper_journal(model, article)) match {
//                case Some(journal) => jours = journal :: jours
//                case _ =>
//            }
//        }
//        Some(new AuthorToJournals(authorinfo, jours))
//    }
//
//    def find(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
//    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
//        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
//            new RuleImpl(
//                name = "icmt",
//                types = List(DblpMetamodel.AUTHOR),
//                from = (m, pattern) => {
//                    my_sleep(sleeping_guard, random.nextInt)
//                    val author = pattern.head.asInstanceOf[DblpAuthor]
//                    val model = m.asInstanceOf[DblpModel]
//                    Some (
//                        helper_hasPapersIST(model, author) & helper_active(model, author)
//                    )
//                },
//                to = List(
//                    new OutputPatternElementImpl(name=PATTERN_AUTHOR_IST,
//                        elementExpr = (_, model, pattern) =>
//                          if (pattern.isEmpty) None else {
//                              my_sleep(sleeping_instantiate, random.nextInt)
//                              val author = pattern.head.asInstanceOf[DblpAuthor]
//                              val active = helper_active(model.asInstanceOf[DblpModel], author)
//                              Some(new AuthorInfoAuthor(author.getName, active = active))
//                          },
//                        outputElemRefs = List (
//                            new OutputPatternElementReferenceImpl(
//                                (tls, _, sm, pattern, output) => {
//                                    my_sleep(sleeping_apply, random.nextInt)
//                                    val author = pattern.head.asInstanceOf[DblpAuthor]
//                                    val model = sm.asInstanceOf[DblpModel]
//                                    val authorinfo = output.asInstanceOf[AuthorInfoAuthor]
//                                    makeAuthorToJournal(tls, model, author, authorinfo)
//                                }
//                            )
//
//                        )
//                    )
//                )
//            ),
//            new RuleImpl(
//                name = "jour",
//                types = List(DblpMetamodel.ARTICLE),
//                from = (m, pattern) => {
//                    my_sleep(sleeping_guard, random.nextInt)
//                    val article = pattern.head.asInstanceOf[DblpArticle]
//                    val model = m.asInstanceOf[DblpModel]
//                    Some(
//                     helper_journal(model, article).indexOf("Information & Software Technology") < 0
//                       & helper_getAuthors(model, article).exists(a => helper_active(model, a)) &
//                       !journals.isDefinedAt(helper_journal(model, article))
//                    )
//                },
//                to = List(
//                    new OutputPatternElementImpl(name=PATTERN_JOURNAL_IST,
//                        elementExpr = (_, model, pattern) => {
//                            my_sleep(sleeping_instantiate, random.nextInt)
//                            val article = pattern.head.asInstanceOf[DblpArticle]
//                            val res = new AuthorInfoJournal(helper_journal(model.asInstanceOf[DblpModel], article))
//                            journals.put(helper_journal(model.asInstanceOf[DblpModel], article), res)
//                            Some(res)
//                        }
//                    )
//                )
//            )
//        ))
}