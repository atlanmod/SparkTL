package org.atlanmod.ttc18.transformation

import org.atlanmod.tl.util.ListUtils
import org.atlanmod.ttc18.model.socialnetwork.element.{SocialNetworkComment, SocialNetworkPost, SocialNetworkSubmission, SocialNetworkUser}
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodel
import org.atlanmod.ttc18.model.socialnetwork.model.SocialNetworkModel

object ScoreHelper {

    def helper_allComments(sub: SocialNetworkSubmission, model: SocialNetworkModel, metamodel: SocialNetworkMetamodel)
    : Seq[SocialNetworkComment] = {
        /*
        helper context SN!Submission def : allComments : Sequence(SN!Comment) =
            self.comments->union(self.comments->collect(e | e.allComments)->flatten());
        */
        val curr_comments = metamodel.getCommentsOfSubmission(model, sub).getOrElse(List())
        val sub_comments = curr_comments.flatMap(c => helper_allComments(c, model, metamodel))
        curr_comments ++ sub_comments
    }

    def helper_countLikes(post: SocialNetworkPost, model: SocialNetworkModel, metamodel: SocialNetworkMetamodel): Int = {
        /*
        helper context SN!Post def : countLikes : Integer =
            self.allComments->collect(e| e.likedBy.size())->sum();
        */
        helper_allComments(post, model, metamodel).map(com => metamodel.getLikedByOfComment(model, com).getOrElse(List()).size).sum
    }

    def helper_scorePost(post: SocialNetworkPost, model: SocialNetworkModel, metamodel: SocialNetworkMetamodel) : Int = {
        /*
        helper context SN!Post def : score : Integer =
            10*self.allComments->size() + self.countLikes;
        */
        10 * helper_allComments(post, model, metamodel).size + helper_countLikes(post, model, metamodel)
    }

    def helper_allFriends(user: SocialNetworkUser, s: Seq[SocialNetworkUser], model: SocialNetworkModel, metamodel: SocialNetworkMetamodel)
    : (Seq[SocialNetworkUser], Seq[SocialNetworkUser]) = {
        /*
        helper def : allFriends(u: SN!User, s:Sequence(SN!User)) : TupleType(component : Sequence(SN!User), remaining : Sequence(SN!User)) =
            if (not s->includes(u))
                then Tuple{component=Sequence{},remaining=s}
            else
                u.friends->iterate(f;
            acc: TupleType(component : Sequence(SN!User), remaining : Sequence(SN!User)) =
            Tuple{component=Sequence{u}, remaining=s->excluding(u)} |
              let ffriends : TupleType(component : Sequence(SN!User), remaining : Sequence(SN!User)) =
            thisModule.allFriends(f,acc.remaining) in
              Tuple{component=acc.component->union(ffriends.component), remaining=ffriends.remaining}
            )
            endif;
        */
        if (!s.contains(user)){
            (Seq(), s)
        }else{
            var acc: (Seq[SocialNetworkUser], Seq[SocialNetworkUser]) = null
            var ffriends: (Seq[SocialNetworkUser], Seq[SocialNetworkUser]) = null
            metamodel.getFriendsOfUser(model, user).getOrElse(List()).foreach(
                f => {
                    acc = (Seq(user), ListUtils.exclude(user, s))
                    ffriends = helper_allFriends(f, acc._2, model, metamodel)
                }
            )
            (acc._1.union(ffriends._1), ffriends._2)
        }
    }

    def helper_allComponents(comment: SocialNetworkComment, model: SocialNetworkModel, metamodel: SocialNetworkMetamodel)
    : Seq[Seq[SocialNetworkUser]] = {
        /*
        helper context SN!Comment def : allComponents : Sequence(Sequence(SN!User)) =
          self.likedBy->iterate(u;
            acc : TupleType(components : Sequence(Sequence(SN!User)), visited : Sequence(SN!User)) =
                Tuple{components=Sequence{}, visited=Sequence{}} |
            if (acc.visited->includes(u))
                then acc
                else let component : TupleType(component : Sequence(SN!User), remaining : Sequence(SN!User)) =
                    thisModule.allFriends(u, self.likedBy->excluding(acc.visited)).component in
                        Tuple{components = acc.components.append(component), visited = acc.visited->union(component)}
            endif).components;
         */
        var acc: (Seq[Seq[SocialNetworkUser]], Seq[SocialNetworkUser]) = null
        var res: (Seq[Seq[SocialNetworkUser]], Seq[SocialNetworkUser]) = null
        metamodel.getLikedByOfComment(model, comment).getOrElse(List()).foreach(
            u => {
                acc = (Seq(), Seq())
                if(acc._2.contains(u)) { res = acc }
                else{
                    val component: (Seq[SocialNetworkUser]) =
                        helper_allFriends(u, metamodel.getLikedByOfComment(model, comment).getOrElse(List()), model, metamodel)._1
                    res = (acc._1 ++ Seq(component), acc._2.union(component))
                }
            }
        )
        res._1
    }

    def helper_scoreComment(post: SocialNetworkComment, model: SocialNetworkModel, metamodel: SocialNetworkMetamodel) : Int = {
        /*
        helper context SN!Comment def : score : Integer =
            self.allComponents->collect(c | c.size()*c.size())->sum();
        */
        helper_allComponents(post, model, metamodel).map(c => c.size * c.size).sum
    }
}
