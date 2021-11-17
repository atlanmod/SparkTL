package org.atlanmod.class2relational.model.relationalmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class RelationalModel(elements: List[RelationalElement] = List(), links: List[RelationalLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: Iterator[RelationalElement] = elements.toIterator
    override def allModelLinks: Iterator[RelationalLink] = links.toIterator

    override def equals(obj: Any): Boolean =
        obj match {
            case model: RelationalModel =>
                ListUtils.eqList(this.allModelLinks.toList, model.allModelLinks.toList) &
                  ListUtils.eqList(this.allModelElements.toList, model.allModelElements.toList)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: RelationalModel =>
                ListUtils.weak_eqList(model.allModelElements.toList, this.allModelElements.toList) &&
                ListUtils.weak_eqList(model.allModelLinks.toList, this.allModelLinks.toList)
            case _ => false
        }

}