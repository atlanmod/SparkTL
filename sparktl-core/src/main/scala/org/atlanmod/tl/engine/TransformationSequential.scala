package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksList
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

object TransformationSequential extends TransformationEngine {

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: List[List[SME]], tls: TraceLinks[SME, TME],
                                                                                        rdd_tls: List[TraceLink[SME, TME]])
    : (Iterable[TME], Iterable[TML]) = {
        val res_rdd: List[(TME, List[TML])] = rdd_tls.map(tl => (tl.getTargetElement, Apply.applyPatternTraces(tr, sm, mm, tl.getSourcePattern, tls)))
        val res: Iterable[(TME, List[TML])] = res_rdd
        (res.map(r => r._1), res.flatMap(r => r._2))
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag, TMC: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int = 0 , sc: SparkContext = null,
     makeModel: (Iterable[TME], Iterable[TML]) => Model[TME, TML, TMC] = (a, b) => ModelUtil.makeTupleModel[TME, TML, TMC](a, b))
    : Model[TME, TML, TMC] = {
        val distr_tls: List[TraceLink[SME, TME]] = allTuplesByRule(tr, sm, mm).flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val source_patterns: List[List[SME]] = distr_tls.map(trace => trace.getSourcePattern)
        val tls: TraceLinks[SME, TME] = new TraceLinksList(distr_tls)

        val output: (Iterable[TME], Iterable[TML]) = applyTraces(tr, sm, mm, source_patterns, tls, distr_tls)
        makeModel(output._1, output._2) // Output format
    }

}