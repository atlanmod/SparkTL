package org.atlanmod

import org.apache.spark.graphx.EdgeTriplet

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

    def makeTripletsFromEdgeTriplets[A](links: List[EdgeTriplet[A, Int]]): List[(A, Int, List[A])] =
        links.map(t => t.toTuple).map(triplet => (triplet._1._2, triplet._3, triplet._2._2)).groupBy(t => (t._1, t._2)).map(t => (t._1._1, t._1._2, t._2.map(v => v._3))).toList

}
