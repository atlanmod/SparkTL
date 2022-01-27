package org.atlanmod.ttc18.model.socialnetwork

import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.link._
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodel

import scala.io.Source

class SocialNetworkCSVLoader {

    // CSV models can be found here:
    // https://github.com/TransformationToolContest/ttc2018liveContest/tree/master/models

    private val date_format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private var raw_links: List[(String, String, String)] /* (src, label, trg) */ = List()

    private def src(v: (String, String, String)) = v._1
    private def label(v: (String, String, String)) = v._2
    private def trg(v: (String, String, String)) = v._3

    private def load_users(csv_file: String, metamodel: SocialNetworkMetamodel) : List[SocialNetworkUser] = {
        // a line: id|name
        val buffer = Source.fromFile(csv_file)
        val res = buffer.getLines().map(line => {
            val cols = line.split(",").map(_.trim)
            new SocialNetworkUser(cols(0), cols(1))
        }).toList
        buffer.close()
        res
    }

    private def load_comments(csv_file: String, metamodel: SocialNetworkMetamodel): List[SocialNetworkComment] = {
        // a line: id|date|content|author_id|post_id
        val buffer = Source.fromFile(csv_file)
        val res = buffer.getLines().map(line => {
            val cols = line.split(",").map(_.trim)
            val id = cols(0)
            val date = date_format.parse(cols(1))
            val content = cols(2)
            val author_id = cols(3)
            val sub_id = cols(4)
            val comment = new SocialNetworkComment(id, date, content)
            raw_links = (author_id, metamodel.USER_SUBMISSIONS, id) :: raw_links
            raw_links = (id, metamodel.SUBMISSION_SUBMITTER, author_id) :: raw_links
            raw_links = (id, metamodel.COMMENT_SUBMISSION, sub_id) :: raw_links
            raw_links = (sub_id, metamodel.SUBMISSION_COMMENTS, id) :: raw_links
            comment
        }).toList
        buffer.close()
        res
    }

    private def load_posts(csv_file: String, metamodel: SocialNetworkMetamodel): List[SocialNetworkPost] = {
        // a line: id|date|content|author_id
        val buffer = Source.fromFile(csv_file)
        val res = buffer.getLines().map(line => {
            val cols = line.split(",").map(_.trim)
            val id = cols(0)
            val date = date_format.parse(cols(1))
            val content = cols(2)
            val author_id = cols(3)
            val post = new SocialNetworkPost(id, date, content)
            raw_links = (author_id, metamodel.USER_SUBMISSIONS, id) :: raw_links
            raw_links = (id, metamodel.SUBMISSION_SUBMITTER, author_id) :: raw_links
            post
        }).toList
        buffer.close()
        res
    }

    private def load_friends(csv_file: String, metamodel: SocialNetworkMetamodel): Unit = {
        // a line: id|name
        val buffer = Source.fromFile(csv_file)
        buffer.getLines().foreach(line => { // TODO get fields
            val cols = line.split(",").map(_.trim)
            val id_user1 = "0"
            val id_user2 = "0"
            raw_links = (id_user1, metamodel.USER_FRIENDS, id_user2) :: raw_links
        })
        buffer.close()
    }

    private def load_likes(csv_file: String, metamodel: SocialNetworkMetamodel): Unit = {
        // a line: id|name
        val buffer = Source.fromFile(csv_file)
        buffer.getLines().foreach(line => { // TODO get fields
            val cols = line.split(",").map(_.trim)
            val id_user = "0"
            val id_comment = "0"
            raw_links = (id_user, metamodel.USER_LIKES, id_comment) :: raw_links
            raw_links = (id_comment, metamodel.COMMENT_LIKEDBY, id_user) :: raw_links
        })
        buffer.close()
    }

    private def buildLink(src: SocialNetworkElement, label: String, trg: List[SocialNetworkElement], metamodel: SocialNetworkMetamodel): SocialNetworkLink = {
        label match {
            case metamodel.SUBMISSION_SUBMITTER => new SubmissionSubmitter(src.asInstanceOf[SocialNetworkSubmission], trg.head.asInstanceOf[SocialNetworkUser])
            case metamodel.USER_SUBMISSIONS => new UserSubmissions(src.asInstanceOf[SocialNetworkUser], trg.asInstanceOf[List[SocialNetworkSubmission]])
            case metamodel.USER_FRIENDS => new UserFriends(src.asInstanceOf[SocialNetworkUser], trg.asInstanceOf[List[SocialNetworkUser]])
            case metamodel.COMMENT_LIKEDBY => new CommentLikedBy(src.asInstanceOf[SocialNetworkComment], trg.asInstanceOf[List[SocialNetworkUser]])
            case metamodel.USER_LIKES => new UserLikes(src.asInstanceOf[SocialNetworkUser], trg.asInstanceOf[List[SocialNetworkComment]])
            case metamodel.COMMENT_POST => new CommentPost(src.asInstanceOf[SocialNetworkComment], trg.head.asInstanceOf[SocialNetworkPost])
            case metamodel.SUBMISSION_COMMENTS => new SubmissionComments(src.asInstanceOf[SocialNetworkSubmission], trg.asInstanceOf[List[SocialNetworkComment]])
            case metamodel.COMMENT_SUBMISSION => new CommentSubmission(src.asInstanceOf[SocialNetworkComment], trg.head.asInstanceOf[SocialNetworkSubmission])
        }
    }

    private def createLinks(elements: List[SocialNetworkElement], metamodel: SocialNetworkMetamodel): List[SocialNetworkLink] = {
        val mapId = elements.map(e => (e.getId, e)).toMap
        raw_links.groupBy(v => (src(v), label(v))).map(entry =>
        {
            val src: Option[SocialNetworkElement] = mapId.get(entry._1._1)
            val label: String = entry._1._2
            val targets: List[Option[SocialNetworkElement]] = entry._2.map(e => mapId.get(trg(e)))
            buildLink(src.get, label, targets.map(o => o.get), metamodel)
        }).toList
    }

    def load(csv_users: String, csv_posts: String, csv_comments: String, csv_friends: String, csv_likes: String,
             metamodel: SocialNetworkMetamodel): SocialNetworkModel = {
        val users = load_users(csv_users, metamodel)
        val posts = load_posts(csv_posts, metamodel)
        val comments = load_comments(csv_comments, metamodel)
        load_friends(csv_friends, metamodel)
        load_likes(csv_likes, metamodel)
        val elements : List[SocialNetworkElement] = users ++ posts ++ comments
        val links : List[SocialNetworkLink] = createLinks(elements, metamodel)
        new SocialNetworkModel(elements, links)
    }

}
