package org.atlanmod.ttc18

import java.util.Date

import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkElement, SocialNetworkLink, SocialNetworkModel}
import org.atlanmod.ttc18.model.socialnetwork.link.{CommentLikedBy, CommentPost, CommentSubmission, SubmissionComments, SubmissionSubmitter, UserFriends, UserLikes, UserSubmissions}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive
import org.atlanmod.ttc18.transformation.ScoreHelper
import org.scalatest.funsuite.AnyFunSuite

class TestHelper extends AnyFunSuite{

    val user1 = new SocialNetworkUser("1","User 1")
    val user2 = new SocialNetworkUser("2","User 2")
    val post1 = new SocialNetworkPost("3", new Date(), "Post content", 0)
    val post2 = new SocialNetworkPost("4", new Date(), "Post content", 0)
    val comment1 = new SocialNetworkComment("5", new Date(), "Comment of Post1", 0)
    val comment2 = new SocialNetworkComment("6", new Date(), "Comment of Comment1", 0)
    val comment3 = new SocialNetworkComment("7", new Date(), "Comment of Comment1", 0)
    val elements : List[SocialNetworkElement] = List(user1, user2, post1, post2, comment1, comment2, comment3)

    def model : SocialNetworkModel = {
        val user1_submissions = new UserSubmissions(user1, List(post1, comment2))
        val user2_submissions = new UserSubmissions(user2, List(post2, comment1, comment3))
        // -----------------------
        val user1_friends = new UserFriends(user1, user2)
        val user2_friends = new UserFriends(user2, user1)
        // -----------------------
        val user1_likes = new UserLikes(user1, List(comment1, comment2))
        val user2_likes = new UserLikes(user2, List(comment2, comment3))
        // ----------------------
        val post1_submitter = new SubmissionSubmitter(post1, user1)
        val post2_submitter = new SubmissionSubmitter(post2, user2)
        val comment1_submitter = new SubmissionSubmitter(comment1, user2)
        val comment2_submitter = new SubmissionSubmitter(comment2, user1)
        val comment3_submitter = new SubmissionSubmitter(comment3, user2)
        // ----------------------
        val post1_comments = new SubmissionComments(post1, List(comment1))
        val comment1_comments = new SubmissionComments(comment1, List(comment2, comment3))
        // ----------------------
        val comment1_post = new CommentPost(comment1, post1)
        val comment2_post = new CommentPost(comment2, post1)
        val comment3_post = new CommentPost(comment3, post1)
        // ----------------------
        val comment1_comment = new CommentSubmission(comment1, post1)
        val comment2_comment = new CommentSubmission(comment2, comment1)
        val comment3_comment = new CommentSubmission(comment3, comment1)
        // ----------------------
        val comment1_likedby = new CommentLikedBy(comment1, user1)
        val comment2_likedby = new CommentLikedBy(comment2, List(user1, user2))
        val comment3_likedby = new CommentLikedBy(comment3, user2)

        val links : List[SocialNetworkLink] = List(user1_submissions, user2_submissions, user1_friends, user2_friends, user1_likes, user2_likes,
            post1_submitter, post2_submitter, comment1_submitter, comment2_submitter, comment3_submitter,
            post1_comments, comment1_comments, comment1_post, comment2_post, comment3_post,
            comment1_comment, comment2_comment, comment3_comment, comment1_likedby, comment2_likedby, comment3_likedby)

        new SocialNetworkModel(elements, links)
    }

    test("test score comment"){
        val metamodel = SocialNetworkMetamodelNaive
        val score = ScoreHelper.helper_score(post1, model, metamodel)
        assert(score == 34)
    }

}
