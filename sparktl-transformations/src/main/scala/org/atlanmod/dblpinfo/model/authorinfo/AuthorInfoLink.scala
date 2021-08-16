package org.atlanmod.dblpinfo.model.authorinfo

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class AuthorInfoLink (type_ : String, source: AuthorInfoElement, target: List[AuthorInfoElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: AuthorInfoLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
              ListUtils.eqList(obj.getTarget, target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = this.equals(o)
}
