package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksArray
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseByRuleWithFold extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : RDD[TML] = {
        sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext)
    : (Double, List[Double], (Int, Int)) = {
        var t1_start : Long = 0
        var t2_start : Long  = 0
        var t3_start : Long = 0
        var t4_start : Long  = 0
        var t5_start : Long  = 0

        var t1_end : Long = 0
        var t2_end : Long  = 0
        var t3_end : Long = 0
        var t4_end : Long  = 0
        var t5_end : Long  = 0

        t1_start = System.nanoTime
        val tuples : RDD[List[SME]] = sc.parallelize(allTuplesByRule(tr, sm, mm))
        val tracelinks : RDD[TraceLink[SME, TME]] = tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        val fold_tls = tracelinks.map(tl => Array(tl)).reduce((a1, a2) => a1 ++ a2)
        val tls : TraceLinks[SME,TME] = new TraceLinksArray(fold_tls)
        val bcast_tls : Broadcast[TraceLinks[SME,TME]] = sc.broadcast(tls)

        val elements: Iterable[TME] = tls.getTargetElements
        t1_end = System.nanoTime

        t2_start = System.nanoTime
        val sps: RDD[List[SME]] = tracelinks.map(trace => trace.getSourcePattern)
        val links: Iterable[TML] = applyTraces(tr, sm, mm, sps, bcast_tls.value).collect()
        t2_end = System.nanoTime

        val t1 = (t1_end - t1_start) * 1000 / 1e9d
        val t2 = (t2_end - t2_start) * 1000 / 1e9d
        val t3 = (t3_end - t3_start) * 1000 / 1e9d
        val t4 = (t4_end - t4_start) * 1000 / 1e9d
        val t5 = (t5_end - t5_start) * 1000 / 1e9d
        val time =  t1 + t2 + t3 + t4 + t5
        (time, List(t1,t2,t3,t4,t5), (elements.size, links.size))
    }

}
