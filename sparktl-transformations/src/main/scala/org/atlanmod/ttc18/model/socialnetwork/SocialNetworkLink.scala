package org.atlanmod.ttc18.model.socialnetwork

import org.atlanmod.tl.model.impl.dynamic.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class SocialNetworkLink(type_ : String, source: SocialNetworkElement, target: List[SocialNetworkElement])
  extends DynamicLink(type_, source, target){

        override def equals(o: Any): Boolean = {
            o match {
                case obj: SocialNetworkLink => type_.equals(obj.getType) & source.equals(obj.getSource) &
                  ListUtils.eqList(obj.getTarget, target)
                case _ => false
            }
        }

        override def weak_equals(o: Any): Boolean = {
            o match {
                case obj: SocialNetworkLink =>
                    source.weak_equals(obj.getSource) &&
                      type_.equals(obj.getType) &&
                      ListUtils.weak_eqList(obj.getTarget, target)
                case _ => false
            }
        }

}