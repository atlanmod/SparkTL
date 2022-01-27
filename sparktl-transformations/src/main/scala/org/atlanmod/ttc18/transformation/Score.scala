package org.atlanmod.ttc18.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils
import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkModel
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.link.{CommentLikedBy, CommentPost, CommentSubmission, SubmissionComments, SubmissionSubmitter, UserFriends, UserLikes, UserSubmissions}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodel

import scala.util.Random

object Score {

    /* In this query, we aim to find comments that are commented by groups of users. We identify groups through the
    friendship relation. Hereby, users that liked a specific comment form a graph where two users are connected if
    they are friends (but still, only users who have liked the comment are considered). The goal of the second query
      is to find strongly connected components in that graph. We assign a score to each comment which is the sum of
      the squared component sizes.
    Similar to the previous query, we aim to find the three comments with the highest score. Ties are broken by
      timestamps. The result string is again a concatenation of the comment ids, separated by the character |. */

    final val PATTERN_USER = "user"
    final val PATTERN_COMMENT = "comment"
    final val PATTERN_POST = "post"

    val random: Random.type = scala.util.Random

    def score(metamodel: SocialNetworkMetamodel,
              scorePost: (SocialNetworkPost, SocialNetworkModel, SocialNetworkMetamodel) => Int = (_, _, _) => 0,
              scoreComment: (SocialNetworkComment, SocialNetworkModel, SocialNetworkMetamodel) => Int= (_, _, _) => 0,
              sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl(
            List(
                new RuleImpl(name="User2User",
                    types = List(metamodel.USER),
                    from = (_, _) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_USER,
                            elementExpr = (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                                val user = pattern.head.asInstanceOf[SocialNetworkUser]
                                Some(new SocialNetworkUser(user.getId, user.getName))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val user = pattern.head.asInstanceOf[SocialNetworkUser]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_user = output.asInstanceOf[SocialNetworkUser]
                                        metamodel.getFriendsOfUser(model, user) match {
                                            case Some(friends) =>
                                                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_USER, metamodel.USER, ListUtils.singletons(friends)) match {
                                                    case Some(new_friends: List[SocialNetworkUser]) =>
                                                        Some(new UserFriends(output_user, new_friends))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val user = pattern.head.asInstanceOf[SocialNetworkUser]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_user = output.asInstanceOf[SocialNetworkUser]
                                        metamodel.getSubmissionsOfUser(model, user) match {
                                            case Some(subs) =>
                                                val comments =
                                                    Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_COMMENT, metamodel.COMMENT, ListUtils.singletons(subs)) match {
                                                        case Some(new_comments: List[SocialNetworkSubmission]) => new_comments
                                                        case _ => List()
                                                    }
                                                val posts =
                                                    Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_POST, metamodel.POST, ListUtils.singletons(subs)) match {
                                                        case Some(new_posts: List[SocialNetworkSubmission]) => new_posts
                                                        case _ => List()
                                                    }
                                                comments ++ posts match {
                                                    case h::t => Some(new UserSubmissions(output_user, h::t))
                                                    case List() => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val user = pattern.head.asInstanceOf[SocialNetworkUser]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_user = output.asInstanceOf[SocialNetworkUser]
                                        metamodel.getLikesOfUser(model, user) match {
                                            case Some(subs) =>
                                                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_COMMENT, metamodel.COMMENT, ListUtils.singletons(subs)) match {
                                                    case Some(new_comments: List[SocialNetworkComment]) =>
                                                        Some(new UserLikes(output_user, new_comments))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    })
                            ))
                    )),
                new RuleImpl(name="Post2Post",
                    types = List(metamodel.POST),
                    from = (_, _) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_POST,
                            elementExpr = (_, sm, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                                val post = pattern.head.asInstanceOf[SocialNetworkPost]
                                val model = sm.asInstanceOf[SocialNetworkModel]
                                Some(new SocialNetworkPost(post.getId, post.getTimestamp, post.getContent, scorePost(post, model, metamodel)))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val post = pattern.head.asInstanceOf[SocialNetworkPost]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_post = output.asInstanceOf[SocialNetworkPost]
                                        metamodel.getSubmitterOfSubmission(model, post) match {
                                            case Some(user: SocialNetworkUser) =>
                                                Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_USER, metamodel.USER, List(user)) match {
                                                    case Some(new_user: SocialNetworkUser) => Some(new SubmissionSubmitter(output_post, new_user))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val post = pattern.head.asInstanceOf[SocialNetworkPost]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_post = output.asInstanceOf[SocialNetworkPost]
                                        metamodel.getCommentsOfSubmission(model, post) match {
                                            case Some(comments: List[SocialNetworkComment]) =>
                                                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_COMMENT, metamodel.COMMENT, ListUtils.singletons(comments)) match {
                                                    case Some(new_comments: List[SocialNetworkComment]) => Some(new SubmissionComments(output_post, new_comments))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    })
                            ))
                    )),
                new RuleImpl(name="Comment2Comment",
                    types = List(metamodel.COMMENT),
                    from = (_, _) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COMMENT,
                            elementExpr = (_, sm, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                                val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                val model = sm.asInstanceOf[SocialNetworkModel]
                                Some(new SocialNetworkComment(comment.getId, comment.getTimestamp, comment.getContent,
                                    scoreComment(comment, model, metamodel)))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_comment = output.asInstanceOf[SocialNetworkComment]
                                        metamodel.getSubmitterOfSubmission(model, comment) match {
                                            case Some(user: SocialNetworkUser) =>
                                                Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_USER, metamodel.USER, List(user)) match {
                                                    case Some(new_user: SocialNetworkUser) => Some(new SubmissionSubmitter(output_comment, new_user))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_comment = output.asInstanceOf[SocialNetworkComment]
                                        metamodel.getCommentsOfSubmission(model, comment) match {
                                            case Some(comments: List[SocialNetworkComment]) =>
                                                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_COMMENT, metamodel.COMMENT, ListUtils.singletons(comments)) match {
                                                    case Some(new_comments: List[SocialNetworkComment]) => Some(new SubmissionComments(output_comment, new_comments))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_comment = output.asInstanceOf[SocialNetworkComment]
                                        metamodel.getLikedByOfComment(model, comment) match {
                                            case Some(users: List[SocialNetworkUser]) =>
                                                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_USER, metamodel.USER, ListUtils.singletons(users)) match {
                                                    case Some(new_users: List[SocialNetworkUser]) => Some(new CommentLikedBy(output_comment, new_users))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_comment = output.asInstanceOf[SocialNetworkComment]
                                        metamodel.getPostOfComment(model, comment) match {
                                            case Some(post) => Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_POST, metamodel.POST, List(post)) match {
                                                case Some(new_post: SocialNetworkPost) =>
                                                    Some(new CommentPost(output_comment, new_post))
                                                case _ => None
                                            }
                                            case _ => None
                                        }
                                    }),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                        val comment = pattern.head.asInstanceOf[SocialNetworkComment]
                                        val model = sm.asInstanceOf[SocialNetworkModel]
                                        val output_comment = output.asInstanceOf[SocialNetworkComment]
                                        metamodel.getSubmissionOfComment(model, comment) match {
                                            case Some(sub) =>
                                                Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_POST, metamodel.POST, List(sub)) match {
                                                    case Some(new_post: SocialNetworkPost) => Some(new CommentSubmission(output_comment, new_post))
                                                    case _ =>
                                                        Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_COMMENT, metamodel.COMMENT, List(sub)) match {
                                                            case Some(new_comment: SocialNetworkComment) => Some(new CommentSubmission(output_comment, new_comment))
                                                            case _ => None
                                                        }
                                                }
                                            case _ => None
                                        }
                                    }
                                ),
                            )
                    ))
            )
        ))
    }
}
