package org.atlanmod.findcouples.model.movie

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class MovieModel(elements: List[MovieElement] = List(), links: List[MovieLink] = List())
extends DynamicModel(elements, links){

    override def allModelElements: Iterator[MovieElement] = elements.toIterator
    override def allModelLinks: Iterator[MovieLink] = links.toIterator

    override def equals(obj: Any): Boolean =
        obj match {
            case model: MovieModel =>
                ListUtils.eqList(this.allModelLinks.toList, model.allModelLinks.toList) &
                  ListUtils.eqList(this.allModelElements.toList, model.allModelElements.toList)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean = equals(obj)

}