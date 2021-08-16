package org.atlanmod.dblpinfo.model.dblp

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class DblpModel (elements: List[DblpElement] = List(), links: List[DblpLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[DblpElement] = elements
    override def allModelLinks: List[DblpLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: DblpModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean = equals(obj)

}
