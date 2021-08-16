package org.atlanmod.dblpinfo.model.authorinfo

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class AuthorInfoModel (elements: List[AuthorInfoElement] = List(), links: List[AuthorInfoLink] = List())
extends DynamicModel(elements, links) {
    override def allModelElements: List[AuthorInfoElement] = elements
        override def allModelLinks: List[AuthorInfoLink] = links

        override def equals(obj: Any): Boolean =
            obj match {
                case model: AuthorInfoModel =>
                    ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                      ListUtils.eqList(this.allModelElements, model.allModelElements)
                case _ => false
            }

        def weak_equals(obj: Any): Boolean = equals(obj)
}