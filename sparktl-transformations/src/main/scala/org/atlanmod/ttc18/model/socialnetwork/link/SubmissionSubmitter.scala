package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class SubmissionSubmitter (source: SocialNetworkSubmission, target: SocialNetworkUser)
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.SUBMISSION_SUBMITTER, source, List(target)) {

    override def getSource: SocialNetworkSubmission = source
    override def getTarget: List[SocialNetworkUser] = List(target)

    def getTargetUser = target
}