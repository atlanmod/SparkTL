package org.atlanmod.dblpinfo.tranformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoConference}
import org.atlanmod.dblpinfo.model.authorinfo.link.AuthorToConferences
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}

import scala.collection.mutable
import scala.util.Random

object InactiveICMTButActiveAuthors {

    final val PATTERN_AUTHOR_ICMT: String = "author_ICMT"
    final val PATTERN_IP_ICMT: String = "ip_ICMT"

    val random: Random.type = scala.util.Random
    val mm: DynamicMetamodel[DynamicElement, DynamicLink] = DblpMetamodel.metamodel

    val conferences: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

    def helper_booktitle(model: DblpModel, ip: DblpInProceedings) : String = ip.getBookTitle

    def helper_year(model: DblpModel, ip: DblpInProceedings) : Int = ip.getYear

    def helper_active(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0 && helper_year(model, ip) > 2008)
            case _ => false
        }

    def helper_nowPublishingIn(model: DblpModel, author: DblpAuthor) : List[DblpInProceedings] =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) => records.filter(r => r.isInstanceOf[DblpInProceedings])
              .map(r => r.asInstanceOf[DblpInProceedings])
              .filter(ip => helper_booktitle(model, ip).indexOf("ICMT") < 0 && helper_year(model, ip) > 2008)
            case _ => List()
        }

    def helper_hasPapersICMT(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0)
            case _ => false
        }

    def helper_getAuthors(m: DblpModel, ip: DblpRecord): List[DblpAuthor] =
        DblpMetamodel.getAuthorsOfRecord(m, ip) match {
            case Some(res) => res
            case None => List()
        }

    def makeAuthorToConference(tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
        val ips = helper_nowPublishingIn(model, input_author)
        var confs: List[AuthorInfoConference] = List()
        for(ip <- ips){
            conferences.get(ip.getBookTitle) match {
                case Some(conf) => confs = conf :: confs
                case _ =>
            }
        }
        Some(new AuthorToConferences(authorinfo, confs))
    }

    def find(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(DblpMetamodel.AUTHOR),
                from = (m, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val author = pattern.head.asInstanceOf[DblpAuthor]
                    val model = m.asInstanceOf[DblpModel]
                    Some(helper_hasPapersICMT(model, author) && !helper_active(model, author))
                },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_AUTHOR_ICMT,
                        elementExpr = (_,model,pattern) => {
                            if (pattern.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt)
                                val author = pattern.head.asInstanceOf[DblpAuthor]
                                val active = helper_active(model.asInstanceOf[DblpModel], author)
                                Some(new AuthorInfoAuthor(author.getName, active = active))
                            }
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
                                    my_sleep(sleeping_apply, random.nextInt)
                                    val author = pattern.head.asInstanceOf[DblpAuthor]
                                    val model = sm.asInstanceOf[DblpModel]
                                    val authorinfo = output.asInstanceOf[AuthorInfoAuthor]
                                    makeAuthorToConference(tls, model, author, authorinfo )
                                }
                            )
                        )
                    )

                )
            ),
            new RuleImpl(
                name = "conf",
                types = List(DblpMetamodel.INPROCEEDINGS),
                from = (model, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val ip = pattern.head.asInstanceOf[DblpInProceedings]
                    val m = model.asInstanceOf[DblpModel]
                    Some(
                        helper_booktitle(m, ip).indexOf("ICMT") < 0 && helper_year(m, ip) > 2008
                          & helper_getAuthors(m, ip).exists(a => helper_hasPapersICMT(m, a) && !helper_active(m, a))
                          & !conferences.isDefinedAt(helper_booktitle(m, ip))
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_IP_ICMT,
                        elementExpr = (_, model, pattern) => {
                            if (pattern.isEmpty) None else {
                                my_sleep(sleeping_apply, random.nextInt)
                                val ip = pattern.head.asInstanceOf[DblpInProceedings]
                                val res = new AuthorInfoConference(helper_booktitle(model.asInstanceOf[DblpModel], ip))
                                conferences.put(ip.getBookTitle, res)
                                Some(res)
                            }
                        }
                    )
                )
            )
        ))
}