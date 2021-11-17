package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils

class ClassModel(elements: List[ClassElement] = List(), links: List[ClassLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: Iterator[ClassElement] = elements.iterator
    override def allModelLinks: Iterator[ClassLink] = links.iterator

    override def equals(obj: Any): Boolean =
        obj match {
            case model: ClassModel =>
                ListUtils.eqList(this.allModelLinks.toList, model.allModelLinks.toList) &
                  ListUtils.eqList(this.allModelElements.toList, model.allModelElements.toList)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: ClassModel =>
                ListUtils.weak_eqList(model.allModelElements.toList, this.allModelElements.toList) &&
                  ListUtils.weak_eqList(model.allModelLinks.toList, this.allModelLinks.toList)
            case _ => false
        }

}

