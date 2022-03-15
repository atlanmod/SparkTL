package org.atlanmod.ttc18.model.socialnetwork.model

import org.apache.spark.graphx.{Edge, EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.atlanmod.Utils
import org.atlanmod.tl.model.impl.GraphModel
import org.atlanmod.tl.util.ListUtils
import org.atlanmod.ttc18.model.socialnetwork.{SocialNetworkElement, SocialNetworkLink}

class SocialNetworkGraphModel(elements: RDD[(VertexId, SocialNetworkElement)], links: RDD[Edge[Int]])
  extends GraphModel[SocialNetworkElement, Int](elements, links) with ISocialNetworkModel {

    override def equals(obj: Any): Boolean =
        obj match {
            case model: SocialNetworkGraphModel  =>
                ListUtils.eqList(this.allModelLinks, model.allModelLinks) &
                  ListUtils.eqList(this.allModelElements, model.allModelElements)
            case _ => false
        }

//     def weak_equals(obj: Any): Boolean = {
//        obj match {
//            case model: SocialNetworkGraphModel =>
//                ListUtils.weak_eqList(this.allModelElements, model.allModelElements) &
//                  ListUtils.weak_eqList(this.allModelLinks, model.allModelLinks)
//            case _ => false
//        }
//    }

}
