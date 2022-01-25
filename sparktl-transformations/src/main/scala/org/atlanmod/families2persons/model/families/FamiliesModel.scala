package org.atlanmod.families2persons.model.families

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class FamiliesModel (elements: List[FamiliesElement] = List(), links: List[FamiliesLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[FamiliesElement] = elements
    override def allModelLinks: List[FamiliesLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: FamiliesModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: FamiliesModel =>
                ListUtils.weak_eqList(model.allModelElements, this.allModelElements) &&
                  ListUtils.weak_eqList(model.allModelLinks, this.allModelLinks)
            case _ => false
        }

}
