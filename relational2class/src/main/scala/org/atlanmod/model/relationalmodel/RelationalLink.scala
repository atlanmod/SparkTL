package org.atlanmod.model.relationalmodel

import org.atlanmod.model.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class RelationalLink (type_ : String, source: RelationalElement, target: List[RelationalElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: RelationalLink => obj.getType.equals(type_) & obj.getSource.equals(source) & obj.getTarget.equals(target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: RelationalLink =>
                obj.getSource.weak_equals(source) &&
                  obj.getType.equals(type_) &&
                  ListUtils.weak_eqList(obj.getTarget, target)
            case _ => false
        }
    }
}