package org.atlanmod.tl.model.impl

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{ParallelTraceLinks, TraceLink}

import scala.reflect.ClassTag

class TraceLinksMapPar[SME, TME: ClassTag] (tls: TraceLinksMap[SME, TME], sc: SparkContext) extends ParallelTraceLinks[SME, TME] {

    val rdd : RDD[(List[SME], List[TraceLink[SME, TME]])] = sc.parallelize(tls.getMap().toSeq)

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        try {
            val filtered = rdd.filter(tl => tl._1.equals(sp))
            filtered.first()._2.find(p)
        } catch {
            case _: Exception => None
        }

    override def getTargetElements: List[TME] = rdd.flatMap(tl => tl._2.map(t => t.getTargetElement)).collect.toList

    override def getSourcePatterns: List[List[SME]] = rdd.map(tl => tl._1).collect.toList

}
