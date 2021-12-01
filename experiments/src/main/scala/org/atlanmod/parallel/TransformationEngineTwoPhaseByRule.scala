package org.atlanmod.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.{ExperimentalTransformationEngine, ModelResult, TimeResult}
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model.impl.TraceLinksArray
import org.atlanmod.tl.model.{Metamodel, Model, TraceLink, TraceLinks, Transformation}

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRule extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : RDD[TML] = sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : (TimeResult, ModelResult[TME, TML]) = {
        val time_result = new TimeResult()

        time_result.start_tuples()
        val tuples = allTuplesByRule(tr, sm, mm)
        time_result.end_tuples()

        time_result.start_instantiate()
        val rddtuples : RDD[List[SME]] = sc.parallelize(tuples)
        val tracelinks : RDD[TraceLink[SME, TME]] = rddtuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val tls : TraceLinks[SME, TME] = new TraceLinksArray(tracelinks.collect())
        time_result.end_instantiate()

        time_result.start_extract()
        val elements: Iterable[TME] = tls.getTargetElements
        time_result.end_extract()

        time_result.start_apply()
        val sps: RDD[List[SME]] = tracelinks.map(trace => trace.getSourcePattern)
        val links: Iterable[TML] = applyTraces(tr, sm, mm, sps, tls).collect()
        time_result.end_apply()

        (time_result, new ModelResult(elements, links))
    }

}
