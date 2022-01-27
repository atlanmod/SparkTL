package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class UserSubmissions  (source: SocialNetworkUser, target: List[SocialNetworkSubmission])
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.USER_SUBMISSIONS, source, target) {

    def this(source: SocialNetworkUser, target: SocialNetworkSubmission) =
        this(source, List(target))

    override def getSource: SocialNetworkUser = source
    override def getTarget: List[SocialNetworkSubmission] = target

}
