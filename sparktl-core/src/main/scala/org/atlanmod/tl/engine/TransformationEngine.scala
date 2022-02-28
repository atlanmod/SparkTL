package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.atlanmod.tl.engine.Trace.{tracePattern, tracePatternWithRule}
import org.atlanmod.tl.engine.Utils.{allTuples, allTuplesByRule, allTuplesByRuleDistinct}
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl.DefaultModel
import org.atlanmod.tl.model.impl.tracelinks.{TraceLinksArray, TraceLinksList, TraceLinksMap}

import scala.reflect.ClassTag

object TransformationEngine {

    private def collect_rdd[A: ClassTag](rdd: RDD[A], config: Parameters.ConfigEngine) : Array[A] =
        config._collect match {
            case Parameters.COLLECT => rdd.collect()
            case Parameters.FOLD =>
                throw new Exception("Fold solution for collecting is not supported yet")
//         TODO rdd.map(tl => Array(tl)).reduce((a1, a2) => a1 ++ a2)
        }

    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[List[SME]], tls: TraceLinks[SME, TME])
    : RDD[TML] = sps.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))


    private def applyTracesWithRule[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                                        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                                        sps: RDD[(List[SME], String)], tls: TraceLinks[SME, TME])
    : RDD[TML] = sps.flatMap(sp => Apply.applyPatternTracesWithRulename(tr, sm, mm, sp, tls))

    def tuple_phase[SME: ClassTag, SML, SMC, SMR, TME : ClassTag, TML: ClassTag]
    (config: Parameters.ConfigEngine, tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
     mm: Metamodel[SME, SML, SMC, SMR], npartition: Int, sc: SparkContext): RDD[List[SME]] = {
        // Create a RDD of tuples, corresponding to potential input of transformation rules, according to the input model
        sc.parallelize(config._tuples match {
            case Parameters.BYRULE => allTuplesByRule(tr, sm, mm)
            case Parameters.BYRULE_DISTINCT => allTuplesByRule(tr, sm, mm).distinct
            case Parameters.BYRULE_UNIQUE => allTuplesByRuleDistinct(tr, sm, mm)
            case Parameters.FULL => allTuples(tr, sm)
            case p => throw new Exception("parameter " + p + " is not supported for generating tuples")
        }, npartition)
    }

    private def build_tracelink[SME: ClassTag, SML, SMC, SMR, TME : ClassTag, TML]
    (config: Parameters.ConfigEngine, trace: RDD[TraceLink[SME, TME]], sc: SparkContext): TraceLinks[SME, TME] = {
        // Build tracelinks from a RDD of TraceLink
        val spark = SparkSession.builder.config(sc.getConf).getOrCreate()
        config._tls match {
            case Parameters.TLS_LIST => new TraceLinksList(collect_rdd(trace, config).toList, config._tracerule)
            case Parameters.TLS_DF =>
                throw new Exception("DataFrame solution for TLS is not supported yet")
//                new TraceLinksDF(spark.createDataFrame(trace, Class[TraceLink[SME, TME]]))
            case Parameters.TLS_ARRAY =>
                new TraceLinksArray(collect_rdd(trace, config), config._tracerule)
            case Parameters.TLS_MAP =>
                new TraceLinksMap(
                    collect_rdd(
                        trace.map(tl => (tl.getSourcePattern, List(tl))).reduceByKey((t1, t2) => t1 ++ t2), config
                    ).toMap, config._tracerule
                )
        }
    }

    def instantiate_phase[SME: ClassTag, SML, SMC, SMR, TME : ClassTag, TML]
    (config: Parameters.ConfigEngine, tuples: RDD[List[SME]], tr: Transformation[SME, SML, SMC, TME, TML],
     sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext): TraceLinks[SME, TME] = {
        // Instantiate output tracelinks containing output element, and a execution trace
        val trace: RDD[TraceLink[SME, TME]] = config._tracerule match {
            case Parameters.WITH_RULE =>
                tuples.flatMap(tuple => tracePatternWithRule(tr, sm, mm, tuple))
            case Parameters.WITHOUT_RULE =>
                tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
        }
        build_tracelink(config, trace, sc)
    }

    def extract_phase[SME: ClassTag, TME : ClassTag](tracelinks: TraceLinks[SME, TME])
    : (Iterable[TME], Seq[Any]) = {
        // Extract output elements, and an iterative sequence for the apply phase
        (tracelinks.getTargetElements, tracelinks.getIterableSeq())
    }

    def apply_phase[SME: ClassTag, SML, SMC, SMR, TME : ClassTag, TML: ClassTag]
    (config: Parameters.ConfigEngine, tracelinks: TraceLinks[SME, TME], tr: Transformation[SME, SML, SMC, TME, TML],
     sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sps: Seq[Any], npartition: Int, sc: SparkContext)
    : Iterable[TML] = {
        collect_rdd[TML]({
            config._tracerule match {
                case Parameters.WITH_RULE =>
                    val rdd_sps = sc.parallelize(sps.asInstanceOf[Seq[(List[SME], String)]], npartition)
                    config._distinctApply match {
                        case Parameters.APPLY_DISTINCT => applyTracesWithRule(tr, sm, mm, rdd_sps.distinct(), tracelinks)
                        case Parameters.APPLY_SIMPLE => applyTracesWithRule(tr, sm, mm, rdd_sps, tracelinks)
                    }
                case Parameters.WITHOUT_RULE =>
                    val rdd_sps = sc.parallelize(sps.asInstanceOf[Seq[List[SME]]], npartition)
                    config._distinctApply match {
                        case Parameters.APPLY_DISTINCT => applyTraces(tr, sm, mm, rdd_sps.distinct(), tracelinks)
                        case Parameters.APPLY_SIMPLE => applyTraces(tr, sm, mm, rdd_sps, tracelinks)
                    }
            }
        }, config)
    }

    def bcast[A: ClassTag](a: A, config: Parameters.ConfigEngine, sc: SparkContext): A = {
        config._bcast match {
            case Parameters.BROADCASTED_TLS => sc.broadcast(a).value
            case Parameters.SHARED_TLS => a
        }
    }

    def execute[SME : ClassTag, SML, SMC, SMR, TME : ClassTag, TML: ClassTag]
    (trans: Transformation[SME, SML, SMC, TME, TML], source: Model[SME, SML], metamodel: Metamodel[SME, SML, SMC, SMR],
     config: Parameters.ConfigEngine, npartition: Int, sc: SparkContext) : DefaultModel[TME, TML] = {
        // 0 - Share source model, metamodel and transformation
        val tr = bcast(trans, config, sc)
        val sm = bcast(source, config, sc)
        val mm = bcast(metamodel, config, sc)

        // 1 - Create tuples
        val tuples : RDD[List[SME]] = tuple_phase(config, tr, sm, mm, npartition, sc)
        // 2 - Instantiate phase
        val tracelinks : TraceLinks[SME, TME] = instantiate_phase(config, tuples, tr, sm, mm, sc)
        // 3 - Output element + source for apply
        val (elements, sps): (Iterable[TME], Seq[Any]) = extract_phase(tracelinks)
        // 4 - Broadcast tracelinks
        val tls = bcast(tracelinks, config, sc)
        // 5 - Apply phase
        val links : Iterable[TML] = apply_phase(config, tls, tr, sm, mm, sps, npartition, sc)
//        config: Parameters.Config, tracelinks: TraceLinks[SME, TME], tr: Transformation[SME, SML, SMC, TME, TML],
//        sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sps: Seq[Any], npartition: Int, sc: SparkContext
        new DefaultModel[TME, TML](elements, links)
    }


}
