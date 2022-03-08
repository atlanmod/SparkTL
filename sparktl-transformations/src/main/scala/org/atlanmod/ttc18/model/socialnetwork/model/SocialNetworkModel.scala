package org.atlanmod.ttc18.model.socialnetwork.model

import org.atlanmod.tl.model.impl.dynamic.DynamicModel
import org.atlanmod.tl.util.ListUtils
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkElement, SocialNetworkLink}

class SocialNetworkModel (elements: List[SocialNetworkElement] = List(), links: List[SocialNetworkLink] = List())
  extends DynamicModel(elements, links) with ISocialNetworkModel{

    override def allModelElements: List[SocialNetworkElement] = elements
    override def allModelLinks: List[SocialNetworkLink] = links

    override def equals(obj: Any): Boolean =
        obj match {
            case model: SocialNetworkModel =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

    def weak_equals(obj: Any): Boolean =
        obj match {
            case model: SocialNetworkModel =>
                ListUtils.weak_eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.weak_eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }
}
