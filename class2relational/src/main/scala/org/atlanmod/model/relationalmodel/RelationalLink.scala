package org.atlanmod.model.relationalmodel

import org.atlanmod.model.DynamicLink
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