package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkSubmission}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class SubmissionComments (source: SocialNetworkSubmission, target: List[SocialNetworkComment])
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.SUBMISSION_COMMENTS, source, target) {

    def this(source: SocialNetworkSubmission, target: SocialNetworkComment) =
        this(source, List(target))

    override def getSource: SocialNetworkSubmission = source
    override def getTarget: List[SocialNetworkComment] = target

}