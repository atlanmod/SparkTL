package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.SocialNetworkUser
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class UserFriends (source: SocialNetworkUser, target: List[SocialNetworkUser])
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.USER_FRIENDS, source, target) {

    def this(source: SocialNetworkUser, target: SocialNetworkUser) =
        this(source, List(target))

    override def getSource: SocialNetworkUser = source
    override def getTarget: List[SocialNetworkUser] = target

}
