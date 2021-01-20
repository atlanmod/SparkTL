package org.atlanmod.tl.model.impl

import org.apache.spark.api.java.{JavaPairRDD, JavaSparkContext}
import org.atlanmod.tl.model.{ParallelTraceLinks, TraceLink}

import scala.collection.{JavaConverters, mutable}

class TraceLinksMapPar[SME, TME] (tls: java.util.List[(List[SME], List[TraceLink[SME, TME]])], jsc: JavaSparkContext) extends ParallelTraceLinks[SME, TME] {

    def this(tl: List[(List[SME], List[TraceLink[SME, TME]])], jsc: JavaSparkContext) = {
        // TODO
        this(JavaConverters.seqAsJavaList(tl), jsc)
    }

    val rdd : JavaPairRDD[List[SME], List[TraceLink[SME, TME]]] = jsc.parallelizePairs(tls)

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] = ???
        // TODO

    override def getTargetElements: List[TME] = ???
    // TODO

    override def getSourcePatterns: List[List[SME]] = ???
    // TODO

    def keys() : Iterable[List[SME]] = ???
    // TODO

    def get(key: List[SME]) : Option[List[TraceLink[SME, TME]]] = ???
    // TODO


    def map(key: List[SME]): mutable.Map[List[SME], List[TraceLink[SME, TME]]] = ???
    // TODO

    def put(key: List[SME], value: TraceLink[SME, TME]): Unit = ???
    // TODO

    def put(key: List[SME], value: List[TraceLink[SME, TME]]): Unit = ???
    // TODO
}
