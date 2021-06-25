package org.atlanmod.class2relational.model.relationalmodel

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class RelationalLink (type_ : String, source: RelationalElement, target: List[RelationalElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: RelationalLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
              ListUtils.eqList(target, obj.getTarget)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: RelationalLink =>
                source.weak_equals(obj.getSource) &
                  type_.equals(obj.getType) &
                  ListUtils.weak_eqList(obj.getTarget, target)
            case _ => false
        }
    }
}