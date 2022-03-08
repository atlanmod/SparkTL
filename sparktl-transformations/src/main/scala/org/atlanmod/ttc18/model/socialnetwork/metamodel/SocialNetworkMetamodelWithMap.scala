package org.atlanmod.ttc18.model.socialnetwork.metamodel
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.model.SocialNetworkModel

object SocialNetworkMetamodelWithMap extends SocialNetworkMetamodel {

    override def getFriendsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkUser]] =
        metamodel.allLinksOfTypeOfElement(user, USER_FRIENDS, model) match {
            case Some(e: List[SocialNetworkUser]) => Some(e)
            case _ => None
        }

    override def getSubmissionsOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkSubmission]] =
        metamodel.allLinksOfTypeOfElement(user, USER_SUBMISSIONS, model) match {
            case Some(e: List[SocialNetworkSubmission]) => Some(e)
            case _ => None
        }

    override def getLikesOfUser(model: SocialNetworkModel, user: SocialNetworkUser): Option[List[SocialNetworkComment]] =
        metamodel.allLinksOfTypeOfElement(user, USER_LIKES, model) match {
            case Some(e: List[SocialNetworkComment]) => Some(e)
            case _ => None
        }

    override def getSubmitterOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[SocialNetworkUser] =
        metamodel.allLinksOfTypeOfElement(sub, SUBMISSION_SUBMITTER, model) match {
            case Some(e: List[SocialNetworkUser]) => Some(e.head)
            case _ => None
        }

    override def getLikedByOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[List[SocialNetworkUser]] =
        metamodel.allLinksOfTypeOfElement(comment, COMMENT_LIKEDBY, model) match {
            case Some(e: List[SocialNetworkUser]) => Some(e)
            case _ => None
        }

    override def getPostOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkPost] =
        metamodel.allLinksOfTypeOfElement(comment, COMMENT_POST, model) match {
            case Some(e: List[SocialNetworkPost]) => Some(e.head)
            case _ => None
        }

    override def getCommentsOfSubmission(model: SocialNetworkModel, sub: SocialNetworkSubmission): Option[List[SocialNetworkComment]] =
        metamodel.allLinksOfTypeOfElement(sub, SUBMISSION_COMMENTS, model) match {
            case Some(e: List[SocialNetworkComment]) => Some(e)
            case _ => None
        }

    override def getSubmissionOfComment(model: SocialNetworkModel, comment: SocialNetworkComment): Option[SocialNetworkSubmission] =
        metamodel.allLinksOfTypeOfElement(comment, COMMENT_SUBMISSION, model) match {
            case Some(e: List[SocialNetworkSubmission]) => Some(e.head)
            case _ => None
        }

}
