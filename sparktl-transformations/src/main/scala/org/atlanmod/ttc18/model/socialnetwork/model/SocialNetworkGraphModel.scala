package org.atlanmod.ttc18.model.socialnetwork.model

import org.apache.spark.graphx.{Edge, EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.atlanmod.Utils
import org.atlanmod.tl.model.impl.GraphModel
import org.atlanmod.tl.util.ListUtils
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkElement, SocialNetworkLink}

class SocialNetworkGraphModel(elements: RDD[(VertexId, SocialNetworkElement)], links: RDD[Edge[Int]])
  extends GraphModel[SocialNetworkElement, Int](elements, links) with ISocialNetworkModel {

    val graph = Graph(elements, links)

    override def allModelElements: List[SocialNetworkElement] =
        graph.vertices.values.collect().toList

    override def allModelLinks: List[EdgeTriplet[SocialNetworkElement, Int]] =
        graph.triplets.collect().toList

    private def makeTripletsFromLinks(links: List[SocialNetworkLink]): List[(SocialNetworkElement, String, SocialNetworkElement)] =
        links.map(link => (link.getSource, link.getType, link.getTarget))
          .flatMap(link => link._3.map(target => (link._1.asInstanceOf[SocialNetworkElement], link._2, target.asInstanceOf[SocialNetworkElement])))

    override def equals(obj: Any): Boolean =
        obj match {
            case model: SocialNetworkGraphModel  =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case model: SocialNetworkModel  =>
                ListUtils.eqList(Utils.makeTripletsFromEdgeTriplets(this.allModelLinks), makeTripletsFromLinks(model.allModelLinks)) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

//    def weak_equals(obj: Any): Boolean =
//        obj match {
//            case model: SocialNetworkGraphModel  =>
//                ListUtils.weak_eqList(this.allModelLinks, model.allModelLinks) &
//                  ListUtils.weak_eqList(this.allModelElements, model.allModelElements)
//            case model: SocialNetworkModel  =>
//                ListUtils.eqList(makeTripletsFromEdgeTriplets(this.allModelLinks), makeTripletsFromLinks(model.allModelLinks)) &
//                  ListUtils.eqList(this.allModelElements, model.allModelElements)
//            case _ => false
//        }

}
