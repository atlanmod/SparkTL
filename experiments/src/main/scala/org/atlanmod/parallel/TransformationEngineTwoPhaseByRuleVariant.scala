package org.atlanmod.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.{ExperimentalTransformationEngine, ModelResult, TimeResult}
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksArray

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRuleVariant extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME],
                                                                                        rdd_tls: RDD[TraceLink[SME, TME]])
    : (Iterable[TME], Iterable[TML]) = {
        val res_rdd : RDD[(TME, List[TML])] = rdd_tls.map(tl => (tl.getTargetElement, Apply.applyPatternTraces(tr, sm, mm, tl.getSourcePattern, tls)))
        val res: Iterable[(TME, List[TML])] = res_rdd.collect()
        (res.map(r => r._1), res.flatMap(r => r._2))
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
        val tuplesrdd : RDD[List[SME]] = sc.parallelize(tuples)
        val tracelinks : RDD[TraceLink[SME, TME]] = tuplesrdd.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val tls : TraceLinks[SME, TME] = new TraceLinksArray(tracelinks.collect())
        time_result.end_instantiate()

        time_result.start_apply()
        val sps: RDD[List[SME]] = tracelinks.map(trace => trace.getSourcePattern)
        val output: (Iterable[TME], Iterable[TML]) = applyTraces(tr, sm, mm, sps, tls, tracelinks)
        time_result.end_apply()

        val elements: Iterable[TME] = output._1
        val links: Iterable[TML] = output._2

        (time_result, new ModelResult(elements, links))
    }

}
