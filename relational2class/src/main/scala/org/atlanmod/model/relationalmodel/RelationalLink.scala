package org.atlanmod.model.relationalmodel

import org.atlanmod.model.DynamicLink

abstract class RelationalLink (type_ : String, source: RelationalElement, target: List[RelationalElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: RelationalLink => obj.getType.equals(type_) & obj.getSource.equals(source) & obj.getTarget.equals(target)
            case _ => false
        }
    }
}