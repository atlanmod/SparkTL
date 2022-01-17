package org.atlanmod.dblpinfo.tranformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.dblpinfo.model.authorinfo.element.{AuthorInfoAuthor, AuthorInfoConference}
import org.atlanmod.dblpinfo.model.authorinfo.link.AuthorToConferences
import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings}
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodel
import org.atlanmod.dblpinfo.tranformation.DblpHelpers._
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}

import scala.collection.mutable
import scala.util.Random

object InactiveICMTButActiveAuthors {

    final val PATTERN_AUTHOR_ICMT: String = "author_ICMT"
    final val PATTERN_IP_ICMT: String = "ip_ICMT"

    val random: Random.type = scala.util.Random

    var conferences: mutable.HashMap[String, AuthorInfoConference] = new mutable.HashMap[String, AuthorInfoConference]()

    def makeAuthorToConference(meta: DblpMetamodel, tls: TraceLinks[DynamicElement, DynamicElement], model: DblpModel,
                               input_author: DblpAuthor, authorinfo: AuthorInfoAuthor): Option[DynamicLink] = {
        val ips = helper_ICMT_nowPublishingIn(model, input_author, meta)
        var confs: List[AuthorInfoConference] = List()
        for(ip <- ips){
            conferences.get(ip.getBookTitle) match {
                case Some(conf) => confs = conf :: confs
                case _ =>
            }
        }
        Some(new AuthorToConferences(authorinfo, confs))
    }

    def find(meta: DblpMetamodel,sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
//        conferences = new mutable.HashMap[String, AuthorInfoConference]()
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(meta.AUTHOR),
                from = (m, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val author = pattern.head.asInstanceOf[DblpAuthor]
                    val model = m.asInstanceOf[DblpModel]
                    Some(helper_hasPapersICMT(model, author, meta) && !helper_active_ICMT(model, author, meta))
                },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_AUTHOR_ICMT,
                        elementExpr = (_,model,pattern) => {
                            if (pattern.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt)
                                val author = pattern.head.asInstanceOf[DblpAuthor]
                                val active = helper_active_ICMT(model.asInstanceOf[DblpModel], author, meta)
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
                                    makeAuthorToConference(meta, tls, model, author, authorinfo)
                                }
                            )
                        )
                    )

                )
            ),
            new RuleImpl(
                name = "conf",
                types = List(meta.INPROCEEDINGS),
                from = (model, pattern) => {
                    my_sleep(sleeping_guard, random.nextInt)
                    val ip = pattern.head.asInstanceOf[DblpInProceedings]
                    val m = model.asInstanceOf[DblpModel]
                    Some(
                        helper_booktitle(m, ip).indexOf("ICMT") < 0 && helper_year(m, ip) > 2008
                          & helper_getAuthors(m, ip, meta).exists(a => helper_hasPapersICMT(m, a, meta) && !helper_active_ICMT(m, a, meta))
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
}