package org.atlanmod.transformation.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.Apply
import org.atlanmod.tl.engine.Trace.tracePattern
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.TraceLinksRDD
import org.atlanmod.transformation.ExperimentalTransformationEngine

import scala.reflect.ClassTag

object TransformationEngineSinglePhaseByRule extends ExperimentalTransformationEngine{

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML, SMC], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: RDD[TraceLink[SME, TME]])
    : (Iterable[TME], Iterable[TML]) = {
        val full_tls = new TraceLinksRDD(tls)
        val res_rdd : RDD[(TME, List[TML])] = tls.map(tl => (tl.getTargetElement, Apply.applyPatternTraces(tr, sm, mm, tl.getSourcePattern, full_tls)))
        val res: Iterable[(TME, List[TML])] = res_rdd.collect()
        (res.map(r => r._1), res.flatMap(r => r._2))
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
        val tracelinks : RDD[TraceLink[SME, TME]] = tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple)).barrier().mapPartitions(identity)
        t1_end = System.nanoTime

        t2_start = System.nanoTime
        val sps: RDD[List[SME]] = tracelinks.map(trace => trace.getSourcePattern)

        val output: (Iterable[TME], Iterable[TML]) = applyTraces(tr, sm, mm, sps, tracelinks)
        t2_end = System.nanoTime

        val elements: Iterable[TME] = output._1
        val links: Iterable[TML] = output._2
        val t1 = (t1_end - t1_start) * 1000 / 1e9d
        val t2 = (t2_end - t2_start) * 1000 / 1e9d
        val t3 = (t3_end - t3_start) * 1000 / 1e9d
        val t4 = (t4_end - t4_start) * 1000 / 1e9d
        val t5 = (t5_end - t5_start) * 1000 / 1e9d
        val time =  t1 + t2 + t3 + t4 + t5
        (time, List(t1,t2,t3,t4,t5), (elements.size, links.size))
    }

}
