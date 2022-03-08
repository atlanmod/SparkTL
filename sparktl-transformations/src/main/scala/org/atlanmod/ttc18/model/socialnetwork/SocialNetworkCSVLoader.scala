package org.atlanmod.ttc18.model.socialnetwork

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.link._
import org.atlanmod.ttc18.model.socialnetwork.metamodel.{SocialNetworkGraphMetamodel, SocialNetworkMetamodel}
import org.atlanmod.ttc18.model.socialnetwork.model.{SocialNetworkGraphModel, SocialNetworkModel}

import scala.io.Source

object SocialNetworkCSVLoader {

    // CSV models can be found here:
    // https://github.com/TransformationToolContest/ttc2018liveContest/tree/master/models

    private val date_format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private var raw_links: List[(String, String, String)] /* (src, label, trg) */ = List()

    private def src(v: (String, String, String)) = v._1
    private def label(v: (String, String, String)) = v._2
    private def trg(v: (String, String, String)) = v._3

    def load_users(csv_file: String, metamodel: SocialNetworkMetamodel) : List[SocialNetworkUser] = {
        // a line: id|name
        val buffer = Source.fromFile(csv_file)
        val res = buffer.getLines().map(line => {
            val cols = line.split(",").map(_.trim)
            new SocialNetworkUser(cols(0), cols(1))
        }).toList
        buffer.close()
        res
    }

     def load_comments(csv_file: String, metamodel: SocialNetworkMetamodel): List[SocialNetworkComment] = {
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

    def load_posts(csv_file: String, metamodel: SocialNetworkMetamodel): List[SocialNetworkPost] = {
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

    def load_friends(csv_file: String, metamodel: SocialNetworkMetamodel): Unit = {
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

    def load_likes(csv_file: String, metamodel: SocialNetworkMetamodel): Unit = {
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

//--------------------------------------

    private var raw_edges: List[(Long, String, Long)] /* (src, label, trg) */ = List()

    def load_users_vertices(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(VertexId, SocialNetworkElement)] =
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.map(row =>
            (row.get(0).asInstanceOf[String].toLong,
              new SocialNetworkUser(row.get(0).asInstanceOf[String], row.get(1).asInstanceOf[String]))
        )

    def load_posts_vertices(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(VertexId, SocialNetworkElement)] = {
        spark.read.format("csv").option("header", header.toString).option("delimiter", delimiter).load(csv_file).rdd.map(row => {
            val id = row(0).asInstanceOf[String]
            val date = date_format.parse(row(1).asInstanceOf[String])
            val content = row(2).asInstanceOf[String]
            val post = new SocialNetworkPost(id, date, content)
            (id.toLong, post)
        }
        )
    }

    def load_comments_vertices(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(VertexId, SocialNetworkElement)] = {
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.map(row =>
            {
                val id = row(0).asInstanceOf[String]
                val date = date_format.parse(row(1).asInstanceOf[String])
                val content = row(2).asInstanceOf[String]
                val author_id = row(3).asInstanceOf[String]
                val sub_id = row(4).asInstanceOf[String]
                val comment = new SocialNetworkComment(id, date, content)
                (id.toLong, comment)
            }
        )
    }

    def load_friends_edges(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(Long, Int, Long)] =
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.map(row =>
            (row(0).asInstanceOf[String].toLong, metamodel.USER_FRIENDS, row(1).asInstanceOf[String].toLong)
        )

    def load_posts_edges(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(Long, Int, Long)] = { // a line: id|date|name|id_user
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.flatMap(row =>
          {try {
              val id_post = row.get(0).asInstanceOf[String].toLong
              val id_user = row.get(3).asInstanceOf[String].toLong
              List(
                  (id_user, metamodel.USER_SUBMISSIONS, id_post),
                  (id_post, metamodel.SUBMISSION_SUBMITTER, id_user)
              )
          }catch{
            case e: Exception => List()
        }
          }
        )
    }

    def load_comments_edges(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(Long, Int, Long)] = { // a line : id|date|name|id_user|id_post
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.flatMap(row =>
        {
            try {
                val id_comment = row.get(0).asInstanceOf[String].toLong
                val id_user = row.get(3).asInstanceOf[String].toLong
                val id_post = row.get(4).asInstanceOf[String].toLong
                List(
                    (id_user, metamodel.USER_SUBMISSIONS, id_comment),
                    (id_comment, metamodel.SUBMISSION_SUBMITTER, id_user),
                    (id_comment, metamodel.COMMENT_SUBMISSION, id_post),
                    (id_post, metamodel.SUBMISSION_COMMENTS, id_comment)
                )
            }catch{
                case _: Exception => List()
            }
          }
        )
    }

    def load_likes_edges(csv_file: String, metamodel: SocialNetworkGraphMetamodel, spark: SparkSession, header: Boolean = false, delimiter: String=";"):
    RDD[(Long, Int, Long)] =
        spark.read.format("csv").option("header",header.toString).option("delimiter", delimiter).load(csv_file).rdd.map(row =>
            (row(0).asInstanceOf[String].toLong, metamodel.USER_LIKES, row(1).asInstanceOf[String].toLong)
        )

    def load_graph(csv_users: String, csv_posts: String, csv_comments: String, csv_friends: String, csv_likes: String,
                   metamodel: SocialNetworkGraphMetamodel, sc: SparkContext, header: Boolean = false, delimiter: String=";"):
    SocialNetworkGraphModel = {

        val spark = SparkSession.builder.config(sc.getConf).getOrCreate()
        val users = load_users_vertices(csv_users, metamodel, spark, header, delimiter)
        val posts = load_posts_vertices(csv_posts, metamodel, spark, header, delimiter)
        val comments = load_comments_vertices(csv_comments, metamodel, spark, header, delimiter)
        val vertices = users.union(posts).union(comments)

        val likes = load_likes_edges(csv_likes, metamodel, spark, header, delimiter)
        val friends = load_friends_edges(csv_friends, metamodel, spark, header, delimiter)
        val comments_edges = load_comments_edges(csv_comments, metamodel, spark, header, delimiter)
        val posts_edges = load_comments_edges(csv_posts, metamodel, spark, header, delimiter)

        val edges = likes.union(friends).union(comments_edges).union(posts_edges).map(triplet => new Edge(triplet._1, triplet._3, triplet._2))
        new SocialNetworkGraphModel(vertices, edges)

    }

}
