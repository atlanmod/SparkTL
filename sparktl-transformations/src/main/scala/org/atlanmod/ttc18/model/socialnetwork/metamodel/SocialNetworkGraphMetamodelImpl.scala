package org.atlanmod.ttc18.model.socialnetwork.metamodel
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Edge
import org.atlanmod.Utils
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.link._
import org.atlanmod.ttc18.model.socialnetwork.model.{SocialNetworkGraphModel, SocialNetworkModel}
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkElement, SocialNetworkLink}

object SocialNetworkGraphMetamodelImpl extends SocialNetworkGraphMetamodel {

    override def getFriendsOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkUser]] = {
        model.graph.triplets
          .filter(t => t.attr.equals(USER_FRIENDS) && t.srcId.equals(user.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkUser]).collect() match {
            case a: Array[SocialNetworkUser] if a.length != 0 => Some(a.toList)
            case _ => None
        }
    }

    override def getSubmissionsOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkSubmission]] = {
        model.graph.triplets
          .filter(t => t.attr.equals(USER_SUBMISSIONS) && t.srcId.equals(user.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkSubmission]).collect() match {
            case a: Array[SocialNetworkSubmission] if a.length != 0 => Some(a.toList)
            case _ => None
        }
    }

    override def getLikesOfUser(model: SocialNetworkGraphModel, user: SocialNetworkUser): Option[List[SocialNetworkComment]] = {
        model.graph.triplets
          .filter(t => t.attr.equals(USER_LIKES) && t.srcId.equals(user.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkComment]).collect() match {
            case a: Array[SocialNetworkComment] if a.length != 0 => Some(a.toList)
            case _ => None
        }
    }

    override def getSubmitterOfSubmission(model: SocialNetworkGraphModel, sub: SocialNetworkSubmission): Option[SocialNetworkUser] = {
        model.graph.triplets
          .filter(t => t.attr.equals(SUBMISSION_SUBMITTER) && t.srcId.equals(sub.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkUser]).collect() match {
            case a: Array[SocialNetworkUser] if a.length != 0 => Some(a.head)
            case _ => None
        }
    }

    override def getLikedByOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[List[SocialNetworkUser]] = {
        model.graph.triplets
          .filter(t => t.attr.equals(COMMENT_LIKEDBY) && t.srcId.equals(comment.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkUser]).collect() match {
            case a: Array[SocialNetworkUser] if a.length != 0 => Some(a.toList)
            case _ => None
        }
    }

    override def getPostOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[SocialNetworkPost] = {
        model.graph.triplets
          .filter(t => t.attr.equals(COMMENT_POST) && t.srcId.equals(comment.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkPost]).collect() match {
            case a: Array[SocialNetworkPost] if a.length != 0 => Some(a.head)
            case _ => None
        }
    }

    override def getCommentsOfSubmission(model: SocialNetworkGraphModel, sub: SocialNetworkSubmission): Option[List[SocialNetworkComment]] = {
        model.graph.triplets
          .filter(t => t.attr.equals(SUBMISSION_COMMENTS) && t.srcId.equals(sub.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkComment]).collect() match {
            case a: Array[SocialNetworkComment] if a.length != 0 => Some(a.toList)
            case _ => None
        }
    }

    override def getSubmissionOfComment(model: SocialNetworkGraphModel, comment: SocialNetworkComment): Option[SocialNetworkSubmission] = {
        model.graph.triplets
          .filter(t => t.attr.equals(COMMENT_SUBMISSION) && t.srcId.equals(comment.getId.toInt))
          .map(t => t.dstAttr.asInstanceOf[SocialNetworkSubmission]).collect() match {
            case a: Array[SocialNetworkSubmission] if a.length != 0 => Some(a.head)
            case _ => None
        }
    }

    private def buildLink(tuple: (SocialNetworkElement, type_edges, List[SocialNetworkElement])): SocialNetworkLink = {
        tuple._2 match {
            case SUBMISSION_SUBMITTER =>
                new SubmissionSubmitter(
                    tuple._1.asInstanceOf[SocialNetworkSubmission],
                    tuple._3.head.asInstanceOf[SocialNetworkUser])
            case USER_SUBMISSIONS =>
                new UserSubmissions(
                    tuple._1.asInstanceOf[SocialNetworkUser],
                    tuple._3.asInstanceOf[List[SocialNetworkSubmission]]
                )
            case USER_FRIENDS =>
                new UserFriends(
                    tuple._1.asInstanceOf[SocialNetworkUser],
                    tuple._3.asInstanceOf[List[SocialNetworkUser]]
                )
            case COMMENT_LIKEDBY =>
                new CommentLikedBy(
                    tuple._1.asInstanceOf[SocialNetworkComment],
                    tuple._3.asInstanceOf[List[SocialNetworkUser]]
                )
            case USER_LIKES =>
                new UserLikes(
                    tuple._1.asInstanceOf[SocialNetworkUser],
                    tuple._3.asInstanceOf[List[SocialNetworkComment]]
                )
            case COMMENT_POST =>
                new CommentPost(
                    tuple._1.asInstanceOf[SocialNetworkComment],
                    tuple._3.head.asInstanceOf[SocialNetworkPost]
                )
            case SUBMISSION_COMMENTS =>
                new SubmissionComments(
                    tuple._1.asInstanceOf[SocialNetworkSubmission],
                    tuple._3.asInstanceOf[List[SocialNetworkComment]]
                )
            case COMMENT_SUBMISSION =>
                new CommentSubmission(
                    tuple._1.asInstanceOf[SocialNetworkComment],
                    tuple._3.head.asInstanceOf[SocialNetworkSubmission]
                )
    }}

    override def buildDynamicModel(model: SocialNetworkGraphModel): SocialNetworkModel = {
        val elements = model.allModelElements
        val links = Utils.makeTripletsFromEdgeTriplets(model.allModelLinks).map(t => buildLink(t))
        new SocialNetworkModel(elements, links)
    }

    private def buildEdges(link: SocialNetworkLink): List[Edge[type_edges]] = {
        val type_link: type_edges = link.getType match {
            case SocialNetworkMetamodelNaive.SUBMISSION_SUBMITTER => SUBMISSION_SUBMITTER
            case SocialNetworkMetamodelNaive.USER_SUBMISSIONS => USER_SUBMISSIONS
            case SocialNetworkMetamodelNaive.USER_FRIENDS => USER_FRIENDS
            case SocialNetworkMetamodelNaive.COMMENT_LIKEDBY => COMMENT_LIKEDBY
            case SocialNetworkMetamodelNaive.USER_LIKES => USER_LIKES
            case SocialNetworkMetamodelNaive.COMMENT_POST => COMMENT_POST
            case SocialNetworkMetamodelNaive.SUBMISSION_COMMENTS => SUBMISSION_COMMENTS
            case SocialNetworkMetamodelNaive.COMMENT_SUBMISSION => COMMENT_SUBMISSION
        }
        link.getTarget.map(target => new Edge(
            link.getSource.asInstanceOf[SocialNetworkElement].getId.toLong,
            target.asInstanceOf[SocialNetworkElement].getId.toLong,
            type_link
        ))
    }

    override def fromDynamicModel(model: SocialNetworkModel, sc: SparkContext): SocialNetworkGraphModel = {
        val elements = sc.parallelize(model.allModelElements.map(element => (element.getId.toLong, element)))
        val links = sc.parallelize(model.allModelLinks.flatMap(link => buildEdges(link)))
        new SocialNetworkGraphModel(elements, links)
    }

}
