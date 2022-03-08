package org.atlanmod.ttc18.model.socialnetwork.metamodel

import org.apache.spark.SparkContext
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.model.{SocialNetworkGraphModel, SocialNetworkModel}

trait SocialNetworkGraphMetamodel extends Serializable {

    type type_edges = Int

    final val USER = 1
    final val SUBMISSION = 2
    final val COMMENT = 3
    final val POST = 4

    final val SUBMISSION_SUBMITTER = 5
    final val USER_SUBMISSIONS = 6
    final val USER_FRIENDS = 7
    final val COMMENT_LIKEDBY = 8
    final val USER_LIKES = 9
    final val COMMENT_POST = 10
    final val SUBMISSION_COMMENTS = 11
    final val COMMENT_SUBMISSION = 12

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("SocialNetworkGraphMetamodel")

    def getFriendsOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkUser]]
    def getSubmissionsOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkSubmission]]
    def getLikesOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkComment]]
    def getSubmitterOfSubmission(model: SocialNetworkGraphModel, sub: SocialNetworkSubmission): Option[SocialNetworkUser]
    def getLikedByOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[List[SocialNetworkUser]]
    def getPostOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[SocialNetworkPost]
    def getCommentsOfSubmission(model: SocialNetworkGraphModel, sub: SocialNetworkSubmission): Option[List[SocialNetworkComment]]
    def getSubmissionOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[SocialNetworkSubmission]

    def buildDynamicModel(model: SocialNetworkGraphModel): SocialNetworkModel
    def fromDynamicModel(model: SocialNetworkModel, sc: SparkContext): SocialNetworkGraphModel
}
