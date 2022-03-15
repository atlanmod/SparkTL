package org.atlanmod

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeTriplet}
import org.atlanmod.tl.model.impl.GraphModel
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicModel}

import scala.reflect.ClassTag

object Utils {

    def my_sleep(millis: Int, rd: Int): Int = {
        var d = rd.toDouble
        val end = System.nanoTime() + millis * 1e6
        var current = System.nanoTime
        while(current < end){
            current = System.nanoTime
            d += 1.0
        }
        d.toInt
    }

    private def makeTripletsFromEdgeTriplets[A, LABEL](links: List[EdgeTriplet[A, LABEL]]): List[(A, LABEL, List[A])] =
        links.map(t => t.toTuple).map(triplet => (triplet._1._2, triplet._3, triplet._2._2)).groupBy(t => (t._1, t._2)).map(t => (t._1._1, t._1._2, t._2.map(v => v._3))).toList

    def buildGraphModel[LABEL: ClassTag](dm: DynamicModel, sc: SparkContext, label: String => LABEL):
    GraphModel[DynamicElement, LABEL] = {
        val elements = sc.parallelize(dm.allModelElements.map(element => (element.getId, element)))
        val links = sc.parallelize(dm.allModelLinks.flatMap(link => link.getTarget.map(
            target => new Edge(
                link.getSource.getId, target.getId, label(link.getType)
            )
        )))
        new GraphModel(elements, links)
    }

    def buildDynamicModel[ME <: DynamicElement, ML <: DynamicLink, LABEL]
    (gm: GraphModel[ME, LABEL], toLink: Tuple3[ME, LABEL, List[ME]] => ML): DynamicModel = {
        val elements: List[DynamicElement] = gm.allModelElements
        val links = makeTripletsFromEdgeTriplets(gm.allModelLinks).map(l => toLink(l))
        new DynamicModel(elements, links)
    }
}
