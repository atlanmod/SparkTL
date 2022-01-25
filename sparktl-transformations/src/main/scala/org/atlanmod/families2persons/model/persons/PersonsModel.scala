package org.atlanmod.families2persons.model.persons

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class PersonsModel (elements: List[PersonsElement] = List()) extends DynamicModel(elements, List()){

    override def allModelElements: List[PersonsElement] = elements

    override def equals(obj: Any): Boolean =
        obj match {
            case model: PersonsModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: PersonsModel =>
                ListUtils.weak_eqList(model.allModelElements, this.allModelElements)
            case _ => false
        }

}
