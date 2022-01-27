package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class UserLikes (source: SocialNetworkUser, target: List[SocialNetworkComment])
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.USER_LIKES, source, target) {

    def this(source: SocialNetworkUser, target: SocialNetworkComment) =
        this(source, List(target))

    override def getSource: SocialNetworkUser = source
    override def getTarget: List[SocialNetworkComment] = target

}
