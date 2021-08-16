package org.atlanmod.dblpinfo.model.dblp

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class DblpLink (type_ : String, source: DblpElement, target: List[DblpElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: DblpLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
                          ListUtils.eqList(obj.getTarget, target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = this.equals(o)

}