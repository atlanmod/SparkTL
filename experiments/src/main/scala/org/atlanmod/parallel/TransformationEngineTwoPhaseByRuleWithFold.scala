package org.atlanmod.parallel

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.atlanmod.{ExperimentalTransformationEngine, ModelResult, TimeResult}
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksArray

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRuleWithFold extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     sps: RDD[List[SME]], tls: TraceLinks[SME, TME]): RDD[TML] =
        sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : (TimeResult, ModelResult[TME, TML]) = {
        val time_result = new TimeResult()

        time_result.start_tuples()
        val tuples = allTuplesByRule(tr, sm, mm)
        time_result.end_tuples()

        time_result.start_instantiate()
        val tuplesrdd : RDD[List[SME]] = sc.parallelize(tuples)
        val tracelinks : RDD[TraceLink[SME, TME]] = tuplesrdd.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val fold_tls = tracelinks.map(tl => Array(tl)).reduce((a1, a2) => a1 ++ a2)
        val tls : TraceLinks[SME,TME] = new TraceLinksArray(fold_tls)
        time_result.end_instantiate()

        time_result.start_broadcast()
        val bcast_tls : Broadcast[TraceLinks[SME,TME]] = sc.broadcast(tls)
        val tls_value = bcast_tls.value
        time_result.end_broadcast()

        time_result.start_extract()
        val elements: Iterable[TME] = tls.getTargetElements
        time_result.end_extract()

        time_result.start_apply()
        val sps: RDD[List[SME]] = tracelinks.map(trace => trace.getSourcePattern)
        val links: Iterable[TML] = applyTraces(tr, sm, mm, sps, tls_value).collect()
        time_result.end_apply()

        (time_result, new ModelResult(elements, links))
    }

}
