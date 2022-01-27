package org.atlanmod.ttc18.model.socialnetwork.link

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkLink
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class CommentLikedBy (source: SocialNetworkComment, target: List[SocialNetworkUser])
  extends SocialNetworkLink(SocialNetworkMetamodelNaive.COMMENT_LIKEDBY, source, target) {

    def this(source: SocialNetworkComment, target: SocialNetworkUser) =
        this(source, List(target))

    override def getSource: SocialNetworkComment = source
    override def getTarget: List[SocialNetworkUser] = target

}