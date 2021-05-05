package org.atlanmod.tl.model.impl

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{ParallelTraceLinks, TraceLink, TraceLinks}
import org.atlanmod.tl.util.ListUtils

import scala.collection.mutable
import scala.reflect.ClassTag

class TraceLinksMapPar[SME, TME: ClassTag] (tls: TraceLinksMap[SME, TME], sc: SparkContext) extends ParallelTraceLinks[SME, TME] {

    val rdd : RDD[(List[SME], List[TraceLink[SME, TME]])] = sc.parallelize(tls.getMap().toSeq)

    def asList(): List[TraceLink[SME, TME]] = tls.asList()

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        try {
            val filtered = rdd.filter(tl => tl._1.equals(sp))
            filtered.first()._2.find(p)
        } catch {
            case _: Exception => None
        }

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] =
    {
        val new_map = new TraceLinksMap(new mutable.HashMap[List[SME], List[TraceLink[SME, TME]]]())
        for (key <- tls.keys){
            tls.get(key) match {
                case Some(tls) =>
                    tls.filter(p) match{
                        case h::t => new_map.put(key, h::t)
                        case _ =>
                    }
                case _ =>
            }
        }
        new TraceLinksMapPar(new_map, sc)
    }

    override def getTargetElements: List[TME] = rdd.flatMap(tl => tl._2.map(t => t.getTargetElement)).collect.toList

    override def getSourcePatterns: List[List[SME]] = rdd.map(tl => tl._1).collect.toList

    override def equals(obj: Any): Boolean = {
        obj match {
            case tl: TraceLinks[SME,TME] => ListUtils.eqList(tl.asList(), this.asList())
            case _ => false
        }
    }

}
