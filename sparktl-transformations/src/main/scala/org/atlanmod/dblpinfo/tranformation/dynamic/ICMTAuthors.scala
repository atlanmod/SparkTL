package org.atlanmod.dblpinfo.tranformation.dynamic

import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoAuthor
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpInProceedings}
import org.atlanmod.dblpinfo.model.dblp.{DblpMetamodel, DblpModel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}

object ICMTAuthors {

    final val PATTERN_AUTHOR_ICMT: String = "author_ICMT"

    def helper_booktitle(model: DblpModel, ip: DblpInProceedings) : String = ip.getBookTitle

    def helper_numOfPapers(model: DblpModel, author: DblpAuthor) : Int =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .count(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0)
            case _ => 0
        }


    def helper_hasPapersICMT(model: DblpModel, author: DblpAuthor) : Boolean =
        DblpMetamodel.getRecordsOfAuthor(model, author) match {
            case Some(records) =>
                records.filter(r => r.isInstanceOf[DblpInProceedings])
                  .map(r => r.asInstanceOf[DblpInProceedings])
                  .exists(ip => helper_booktitle(model, ip).indexOf("ICMT") >= 0)
            case _ => false
        }

    def find: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(DblpMetamodel.AUTHOR),
                from = (model, pattern) =>
                    Some(helper_hasPapersICMT(model.asInstanceOf[DblpModel], pattern.head.asInstanceOf[DblpAuthor])),
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_AUTHOR_ICMT,
                        elementExpr = (_,model,pattern) => {
                            if (pattern.isEmpty) None else {
                                val author = pattern.head.asInstanceOf[DblpAuthor]
                                val numOfPapers = helper_numOfPapers(model.asInstanceOf[DblpModel], author)
                                Some(new AuthorInfoAuthor(author.getName, numOfPapers = numOfPapers))
                            }
                        }
                    )
                )
            )
        ))

}
