package org.atlanmod.tl.model.impl

import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{TraceLink, TraceLinks}
import org.atlanmod.tl.util.ListUtils

import scala.reflect.ClassTag

class TraceLinksArrayPar[SME: ClassTag, TME: ClassTag](tls: RDD[TraceLink[SME, TME]]) extends TraceLinks[SME, TME] {

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] = {
        val filtered_rdd =  tls.filter(tl=> tl.getSourcePattern.equals(sp) && p(tl))
        if (filtered_rdd.isEmpty()) None
        else Some(filtered_rdd.first())
    }

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] =
        new TraceLinksArrayPar(tls.filter(tl => p(tl)))

    override def getTargetElements: Iterable[TME] = tls.map(tl => tl.getTargetElement).collect()

    override def getSourcePatterns: Iterable[List[SME]] = tls.map(tl => tl.getSourcePattern).collect()

    def getTargetElementsAsRDD: RDD[TME] = tls.map(tl => tl.getTargetElement)

    def getSourcePatternsAsRDD: RDD[List[SME]] = tls.map(tl => tl.getSourcePattern)

    def asList(): List[TraceLink[SME, TME]] = tls.collect().toList

    override def equals(obj: Any): Boolean = {
        obj match {
            case tl: TraceLinks[SME,TME] => ListUtils.eqList(tl.asList(), this.asList())
            case _ => false
        }
    }
}