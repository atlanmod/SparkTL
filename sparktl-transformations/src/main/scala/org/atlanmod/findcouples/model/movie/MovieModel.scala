package org.atlanmod.findcouples.model.movie

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class MovieModel(elements: List[MovieElement] = List(), links: List[MovieLink] = List())
extends DynamicModel(elements, links){

    override def allModelElements: List[MovieElement] = elements
    override def allModelLinks: List[MovieLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: MovieModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean = equals(obj)

}