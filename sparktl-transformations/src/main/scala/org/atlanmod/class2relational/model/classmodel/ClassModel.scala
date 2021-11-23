package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class ClassModel(elements: List[ClassElement] = List(), links: List[ClassLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[ClassElement] = elements
    override def allModelLinks: List[ClassLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: ClassModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: ClassModel =>
                ListUtils.weak_eqList(model.allModelElements, this.allModelElements) &&
                  ListUtils.weak_eqList(model.allModelLinks, this.allModelLinks)
            case _ => false
        }

}

