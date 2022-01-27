package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class CommentPost (source: SocialNetworkComment, target: SocialNetworkPost)
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.COMMENT_POST, source, List(target)) {

    override def getSource: SocialNetworkComment = source
    override def getTarget: List[SocialNetworkPost] = List(target)

    def getTargetPost = target
}