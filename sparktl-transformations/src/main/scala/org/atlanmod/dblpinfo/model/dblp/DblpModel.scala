package org.atlanmod.dblpinfo.model.dblp

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class DblpModel (elements: List[DblpElement] = List(), links: List[DblpLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: Iterator[DblpElement] = elements.toIterator
    override def allModelLinks: Iterator[DblpLink] = links.toIterator

    override def equals(obj: Any): Boolean =
        obj match {
            case model: DblpModel =>
                ListUtils.eqList(this.allModelLinks.toList, model.allModelLinks.toList) &
                  ListUtils.eqList(this.allModelElements.toList, model.allModelElements.toList)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean = equals(obj)

}
