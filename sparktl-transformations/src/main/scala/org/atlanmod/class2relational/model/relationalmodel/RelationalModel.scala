package org.atlanmod.class2relational.model.relationalmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class RelationalModel(elements: List[RelationalElement] = List(), links: List[RelationalLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[RelationalElement] = elements
    override def allModelLinks: List[RelationalLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: RelationalModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: RelationalModel =>
                ListUtils.weak_eqList(model.allModelElements, this.allModelElements) &&
                ListUtils.weak_eqList(model.allModelLinks, this.allModelLinks)
            case _ => false
        }

}