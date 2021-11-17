package org.atlanmod.dblpinfo.model.authorinfo

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class AuthorInfoModel (elements: List[AuthorInfoElement] = List(), links: List[AuthorInfoLink] = List())
extends DynamicModel(elements, links) {

    override def allModelElements: Iterator[AuthorInfoElement] = elements.iterator

    override def allModelLinks: Iterator[AuthorInfoLink] = links.iterator

    override def equals(obj: Any): Boolean =
        obj match {
            case model: AuthorInfoModel =>
                ListUtils.eqList(this.allModelLinks.toList, model.allModelLinks.toList) &
                  ListUtils.eqList(this.allModelElements.toList, model.allModelElements.toList)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean = equals(obj)
}