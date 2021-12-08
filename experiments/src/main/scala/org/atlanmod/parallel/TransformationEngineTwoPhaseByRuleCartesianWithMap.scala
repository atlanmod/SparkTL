package org.atlanmod.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksMap
import org.atlanmod.{ExperimentalTransformationEngine, ModelResult, TimeResult}

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRuleCartesianWithMap extends ExperimentalTransformationEngine{

    private def allTuplesOfTypes_cartesian[SME: ClassTag, SML, SMC, SMR, TME, TML, TMC]
    (inTypes: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext, npartition: Int)
    : RDD[List[SME]] = {
        inTypes.map(type_ => sc.parallelize(mm.allModelElementsOfType(type_, sm), npartition)) match {
            case List() => sc.parallelize(List())
            case List(h) => h.map(sme => List(sme))
            case h1 :: h2 :: t =>
                val start = h1.cartesian(h2).map(t => List(t._1, t._2))
                t.foldLeft(start)((acc, t2) => acc.cartesian(t2).map(t => t._1 ++ List(t._2)))
        }
    }

    def allTuplesByRule_cartesian[SME: ClassTag, SML, SMC, SMR, TME, TML, TMC]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     sc: SparkContext, npartition: Int): RDD[List[SME]] =
        tr.getRules.map(r => allTuplesOfTypes_cartesian(r.getInTypes, sm, mm, sc, npartition)).reduce(_ union _)

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : Iterable[TML] = {
        sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : (TimeResult, ModelResult[TME, TML]) = {
        val time_result = new TimeResult()

        time_result.start_instantiate()
        val tuples_rdd : RDD[List[SME]] = allTuplesByRule_cartesian(tr, sm, mm, sc, npartition)
        val instantiated_tracelinks : RDD[TraceLink[SME, TME]] = tuples_rdd.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val tls : TraceLinks[SME, TME] = new TraceLinksMap(
            instantiated_tracelinks.map(tl => (tl.getSourcePattern, List(tl))).reduceByKey((t1, t2) => t1 ++ t2).collect.toMap
        )
        time_result.end_instantiate()

        time_result.start_extract()
        val elements : Iterable[TME] = tls.getTargetElements
        time_result.end_extract()

        time_result.start_apply()
        val source_patterns: RDD[List[SME]] = instantiated_tracelinks.map(trace => trace.getSourcePattern)
        val links: Iterable[TML] = applyTraces(tr, sm, mm, source_patterns, tls)
        time_result.end_apply()

        (time_result, new ModelResult(elements, links))
    }

}
