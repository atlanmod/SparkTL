package org.atlanmod.ttc18.model.socialnetwork.metamodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkModel
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}

trait SocialNetworkMetamodel extends Serializable {

    final val USER = "User"
    final val SUBMISSION = "Submission"
    final val COMMENT = "Comment"
    final val POST = "Post"

    final val SUBMISSION_SUBMITTER = "submitter"
    final val USER_SUBMISSIONS = "submissions"
    final val USER_FRIENDS = "friends"
    final val COMMENT_LIKEDBY = "likedBy"
    final val USER_LIKES = "likes"
    final val COMMENT_POST = "post"
    final val SUBMISSION_COMMENTS = "comments"
    final val COMMENT_SUBMISSION = "commented"

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("SocialNetworkMetamodel")

    def getFriendsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkUser]]
    def getSubmissionsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkSubmission]]
    def getLikesOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkComment]]
    def getSubmitterOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[SocialNetworkUser]
    def getLikedByOfComment(model:SocialNetworkModel, comment: SocialNetworkComment): Option[List[SocialNetworkUser]]
    def getPostOfComment(model:SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkPost]
    def getCommentsOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[List[SocialNetworkComment]]
    def getSubmissionOfComment(model:SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkSubmission]
}
