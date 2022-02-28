package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model.impl.DefaultModel
import org.atlanmod.tl.model.impl.tracelinks.TraceLinksMap
import org.atlanmod.tl.model.{Metamodel, Model, TraceLink, TraceLinks, Transformation}

import scala.reflect.ClassTag

object TransformationParallel {

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : RDD[TML] = sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag, TMC: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : Model[TME, TML] = {

        val rdd_tls: RDD[TraceLink[SME, TME]] = sc.parallelize(allTuplesByRule(tr, sm, mm), npartition).flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val source_patterns: RDD[List[SME]] = rdd_tls.map(trace => trace.getSourcePattern)

        val tls: TraceLinks[SME, TME] = // Make TraceLinks from distr_tls in a "map o reduce" operation
            new TraceLinksMap(
                rdd_tls.map(tl => (tl.getSourcePattern, List(tl))).reduceByKey((t1, t2) => t1 ++ t2).collect.toMap
            )

        val elements : Iterable[TME] = tls.getTargetElements
        val links: Iterable[TML] = applyTraces(tr, sm, mm, source_patterns, tls).collect.distinct
        new DefaultModel(elements, links) // Output format
    }

}