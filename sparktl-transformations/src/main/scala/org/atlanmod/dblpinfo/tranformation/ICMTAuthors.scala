package org.atlanmod.dblpinfo.tranformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.dblpinfo.model.authorinfo.element.AuthorInfoAuthor
import org.atlanmod.dblpinfo.model.dblp.DblpModel
import org.atlanmod.dblpinfo.model.dblp.element.DblpAuthor
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodel
import org.atlanmod.dblpinfo.tranformation.DblpHelpers.{helper_hasPapersICMT, helper_numOfPapers}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}

import scala.util.Random

object ICMTAuthors {

    final val PATTERN_AUTHOR_ICMT: String = "author_ICMT"

    val random: Random.type = scala.util.Random

    def find(meta: DblpMetamodel,sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](List(
            new RuleImpl(
                name = "icmt",
                types = List(meta.AUTHOR),
                from = (model, pattern) => {
                        my_sleep(sleeping_guard, random.nextInt)
                        Some(helper_hasPapersICMT(model.asInstanceOf[DblpModel], pattern.head.asInstanceOf[DblpAuthor], meta))
                    },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_AUTHOR_ICMT,
                        elementExpr = (_,model,pattern) => {
                            if (pattern.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt)
                                val author = pattern.head.asInstanceOf[DblpAuthor]
                                val numOfPapers = helper_numOfPapers(model.asInstanceOf[DblpModel], author, meta)
                                Some(new AuthorInfoAuthor(author.getName, numOfPapers = numOfPapers, true))
                            }
                        }
                    )
                )
            )
        ))

}
