package org.atlanmod.findcouples.model.movie

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class MovieLink(type_ : String, source: MovieElement, target: List[MovieElement])
      extends DynamicLink(type_, source, target){

        override def equals(o: Any): Boolean = {
            o match {
                case obj: MovieLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
                  ListUtils.eqList(obj.getTarget, target)
                case _ => false
            }
        }

        override def weak_equals(o: Any): Boolean = this.equals(o)

}
