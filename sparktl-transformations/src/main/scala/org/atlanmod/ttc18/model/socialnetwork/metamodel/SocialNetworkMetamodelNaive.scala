package org.atlanmod.ttc18.model.socialnetwork.metamodel
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkLink, SocialNetworkModel}
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.link.{CommentLikedBy, CommentPost, CommentSubmission, SubmissionComments, SubmissionSubmitter, UserFriends, UserLikes, UserSubmissions}

object SocialNetworkMetamodelNaive extends SocialNetworkMetamodel {

    def getFriendsOfUserOnLinks(iterator: Iterator[SocialNetworkLink], user: SocialNetworkUser): Option[List[SocialNetworkUser]] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: UserFriends =>
                    if (h.getSource.equals(user)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getSubmissionsOfUserOnLinks(iterator: Iterator[SocialNetworkLink], user: SocialNetworkUser): Option[List[SocialNetworkSubmission]] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: UserSubmissions =>
                    if (h.getSource.equals(user)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getLikesOfUserOnLinks(iterator: Iterator[SocialNetworkLink], user: SocialNetworkUser): Option[List[SocialNetworkComment]] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: UserLikes =>
                    if (h.getSource.equals(user)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getSubmitterOfSubmissionOnLinks(iterator: Iterator[SocialNetworkLink], sub: SocialNetworkSubmission): Option[SocialNetworkUser] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: SubmissionSubmitter =>
                    if (h.getSource.equals(sub)) return Some(h.getTargetUser)
                case _ =>
            }
        }
        None
    }

    def getLikedByOfCommentOnLinks(iterator: Iterator[SocialNetworkLink], comment: SocialNetworkComment): Option[List[SocialNetworkUser]] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: CommentLikedBy =>
                    if (h.getSource.equals(comment)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getPostOfCommentOnLinks(iterator: Iterator[SocialNetworkLink], comment: SocialNetworkComment): Option[SocialNetworkPost] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: CommentPost =>
                    if (h.getSource.equals(comment)) return Some(h.getTargetPost)
                case _ =>
            }
        }
        None
    }

    def getCommentsOfSubmissionOnLinks(iterator: Iterator[SocialNetworkLink], sub: SocialNetworkSubmission): Option[List[SocialNetworkSubmission]] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: SubmissionComments =>
                    if (h.getSource.equals(sub)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getSubmissionOfCommentOnLinks(iterator: Iterator[SocialNetworkLink], comment: SocialNetworkComment): Option[SocialNetworkSubmission] = {
        while(iterator.hasNext){
            val value = iterator.next()
            value match {
                case h: CommentSubmission =>
                    if (h.getSource.equals(comment)) return Some(h.getTargetSubmission)
                case _ =>
            }
        }
        None
    }

    override def getFriendsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkUser]] =
        getFriendsOfUserOnLinks(model.allModelLinks.toIterator, user)

    override def getSubmissionsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkSubmission]] =
        getSubmissionsOfUserOnLinks(model.allModelLinks.toIterator, user)

    override def getLikesOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkComment]] =
        getLikesOfUserOnLinks(model.allModelLinks.toIterator, user)

    override def getSubmitterOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[SocialNetworkUser] =
        getSubmitterOfSubmissionOnLinks(model.allModelLinks.toIterator, sub)

    override def getLikedByOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[List[SocialNetworkUser]] =
        getLikedByOfCommentOnLinks(model.allModelLinks.toIterator, comment)

    override def getPostOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkPost] =
        getPostOfCommentOnLinks(model.allModelLinks.toIterator, comment)

    override def getCommentsOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[List[SocialNetworkSubmission]] =
        getCommentsOfSubmissionOnLinks(model.allModelLinks.toIterator, sub)

    override def getSubmissionOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkSubmission] =
        getSubmissionOfCommentOnLinks(model.allModelLinks.toIterator, comment)
}
