package org.atlanmod.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.{ExperimentalTransformationEngine, ModelResult, TimeResult}
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksMap

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRuleWithMap extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME],
                                                                                        rdd_tls: RDD[TraceLink[SME, TME]])
    : Iterable[TML] = {
        sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect.distinct
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : (TimeResult, ModelResult[TME, TML]) = {
        val time_result = new TimeResult()

        time_result.start_tuples()
        val tuples = allTuplesByRule(tr, sm, mm)
        time_result.end_tuples()

        time_result.start_instantiate()
        val tuples_rdd : RDD[List[SME]] = sc.parallelize(tuples, npartition)
        val instantiated_tracelinks : RDD[TraceLink[SME, TME]] = tuples_rdd.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val tls : TraceLinks[SME, TME] = new TraceLinksMap(
            instantiated_tracelinks.map(tl => (tl.getSourcePattern, List(tl))).reduceByKey((t1, t2) => t1 ++ t2).collect.toMap
        )
        time_result.end_instantiate()

        time_result.start_extract()
        val elements : Iterable[TME] = tls.getTargetElements
        time_result.end_extract()

        time_result.start_apply()
        val source_patterns: RDD[List[SME]] = instantiated_tracelinks.map(trace => trace.getSourcePattern).distinct()
        val links: Iterable[TML] = applyTraces(tr, sm, mm, source_patterns, tls, instantiated_tracelinks)
        time_result.end_apply()

        (time_result, new ModelResult(elements, links))
    }

}
