package org.atlanmod.families2persons.model.families

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

class FamiliesLink (type_ : String, source: FamiliesElement, target: List[FamiliesElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: FamiliesLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
              ListUtils.eqList(obj.getTarget, target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: FamiliesLink =>
                source.weak_equals(obj.getSource) &&
                  type_.equals(obj.getType) &&
                  ListUtils.weak_eqList(obj.getTarget, target)
            case _ => false
        }
    }

}
