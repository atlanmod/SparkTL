package org.atlanmod.tl.model.impl

import org.apache.spark.graphx.{Edge, EdgeTriplet, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.Model

import scala.reflect.ClassTag

class GraphModel[ME: ClassTag, LABEL: ClassTag](elements: RDD[(VertexId, ME)], links: RDD[Edge[LABEL]])
  extends Model[ME, EdgeTriplet[ME, LABEL]]{

  val graph = Graph(elements, links)

  override def allModelElements: List[ME] = elements.map(t => t._2).collect().toList
  override def allModelLinks: List[EdgeTriplet[ME, LABEL]] = graph.triplets.collect().toList

}
