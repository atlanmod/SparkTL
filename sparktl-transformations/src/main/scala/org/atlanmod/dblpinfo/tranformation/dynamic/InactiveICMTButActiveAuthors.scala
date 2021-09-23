package org.atlanmod.dblpinfo.tranformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.AuthorInfoMetamodel
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoConference}
import org.atlanmod.dblpinfo.model.authorinfo.link.AuthorToConferences
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings, DblpRecord}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.util.ListUtils

import scala.collection.mutable

object InactiveICMTButActiveAuthors {

    final val PATTERN_AUTHOR_ICMT: String = "author_ICMT"
    final val PATTERN_IP_ICMT: String = "ip_ICMT"

    val mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def conferences: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

    def helper_booktitle(model: DblpModel, ip: DblpInProceedings) : String = ip.getBookTitle

    def helper_year(model: DblpModel, ip: DblpInProceedings) : Int = ip.getYear

    def helper_active(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author)
          .filter(r => r.isInstanceOf[DblpInProceedings])
          .map(r => r.asInstanceOf[DblpInProceedings])
          .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") > 0 && helper_year(model, ip) > 2008)

    def helper_nowPublishingIn(model: DblpModel, author: DblpAuthor) : List[DblpInProceedings] =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) => records.filter(r => r.isInstanceOf[DblpInProceedings])
              .map(r => r.asInstanceOf[DblpInProceedings])
              .filter(ip => helper_booktitle(model, ip).indexOf("ICMT") > 0 && helper_year(model, ip) > 2008)
            case _ => List()
        }

    def helper_hasPapersICMT(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author)
          .filter(r => r.isInstanceOf[DblpInProceedings])
          .map(r => r.asInstanceOf[DblpInProceedings])
          .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") > 0)

    def helper_getAuthors(m: DblpModel, ip: DblpRecord): List[DblpAuthor] =
        DblpMetamodel.getAuthorsOfRecord(m, ip) match {
            case Some(res) => res
            case None => List()
        }

    def makeAuthorToConference(tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
        val ips = helper_nowPublishingIn(model, input_author)
        Resolve.resolveAll(tls, model, mm, PATTERN_IP_ICMT, AuthorInfoMetamodel.CONFERENCE , ListUtils.singletons(ips)) match {
            case Some(conferences: List[AuthorInfoConference]) =>
                Some(new AuthorToConferences(authorinfo, conferences))
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
                    Some(helper_hasPapersICMT(model, author) && !helper_active(model, author))
                },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_AUTHOR_ICMT,
                        elementExpr = (_,model,pattern) => {
                            if (pattern.isEmpty) None else {
                                val author = pattern.head.asInstanceOf[DblpAuthor]
                                val active = helper_active(model.asInstanceOf[DblpModel], author)
                                Some(new AuthorInfoAuthor(author.getName, active = active))
                            }
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
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
                    val ip = pattern.head.asInstanceOf[DblpInProceedings]
                    val m = model.asInstanceOf[DblpModel]
                    Some(
                        helper_booktitle(m, ip).indexOf("ICMT") >= 0
                          & helper_getAuthors(m, ip).exists(a => helper_active(m, a))
                    )
                },
                to = List(
                    new OutputPatternElementImpl(name=PATTERN_IP_ICMT,
                        elementExpr = (_, model, pattern) => {
                            if (pattern.isEmpty) None else {
                                val ip = pattern.head.asInstanceOf[DblpInProceedings]
                                Some(new AuthorInfoConference(helper_booktitle(model.asInstanceOf[DblpModel], ip)))
                            }
                        }
                    )
                )
            )
        ))
}