package org.atlanmod.tl.model.impl

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{ParallelTraceLinks, TraceLink}

class TraceLinksListPar[SME, TME](tls: List[TraceLink[SME, TME]], sc: SparkContext) extends ParallelTraceLinks[SME, TME] {

    val rdd : RDD[TraceLink[SME, TME]] = sc.parallelize(tls)

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] = {
        try {
            Some(rdd.filter(tl => tl.getSourcePattern.equals(sp) && p(tl)).first())
        } catch {
            case _: Exception => None
        }
    }

    override def getTargetElements: List[TME] = {
        rdd.map(tl => tl.getTargetElement).collect().toList
    }

    override def getSourcePatterns: List[List[SME]] = {
        rdd.map(tl => tl.getSourcePattern).collect().toList
    }

}