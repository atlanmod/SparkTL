package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model.impl.TraceLinksMap
import org.atlanmod.tl.model.{Metamodel, Model, TraceLink, TraceLinks, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

object TransformationParallel extends TransformationEngine {

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
     npartition: Int, sc: SparkContext,
     makeModel: (Iterable[TME], Iterable[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML] = {
        val distr_tls : RDD[TraceLink[SME, TME]] = sc.parallelize(allTuplesByRule(tr, sm, mm)).flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val source_patterns: RDD[List[SME]] = distr_tls.map(trace => trace.getSourcePattern)

        val tls : TraceLinks[SME, TME] = // Make TraceLinks from distr_tls in a "map o reduce" operation
            new TraceLinksMap(
                distr_tls.map(tl => (tl.getSourcePattern, List(tl))).reduceByKey((t1, t2) => t1 ++ t2).collect.toMap
            )

        val output: (Iterable[TME], Iterable[TML]) = applyTraces(tr, sm, mm, source_patterns, tls, distr_tls)
        makeModel(output._1, output._2) // Output format
    }

}