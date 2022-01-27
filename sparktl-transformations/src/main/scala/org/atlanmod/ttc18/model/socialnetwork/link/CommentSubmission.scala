package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkSubmission}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class CommentSubmission(source: SocialNetworkComment, target: SocialNetworkSubmission)
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.COMMENT_SUBMISSION, source, List(target)) {

    override def getSource: SocialNetworkComment = source
    override def getTarget: List[SocialNetworkSubmission] = List(target)

    def getTargetSubmission = target
}