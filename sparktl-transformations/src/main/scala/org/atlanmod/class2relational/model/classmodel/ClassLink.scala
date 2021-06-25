package org.atlanmod.class2relational.model.classmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class ClassLink(type_ : String, source: ClassElement, target: List[ClassElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ClassLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
              ListUtils.eqList(obj.getTarget, target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassLink =>
                source.weak_equals(obj.getSource) &&
                  type_.equals(obj.getType) &&
                  ListUtils.weak_eqList(obj.getTarget, target)
            case _ => false
        }
    }



}
