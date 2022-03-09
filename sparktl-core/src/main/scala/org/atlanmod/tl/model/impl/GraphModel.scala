package org.atlanmod.tl.model.impl

import org.apache.spark.graphx.{Edge, EdgeTriplet, VertexId}
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.Model

abstract class GraphModel[ME, LABEL](elements: RDD[(VertexId, ME)], links: RDD[Edge[LABEL]])
  extends Model[ME, EdgeTriplet[ME, Int]]{

}
