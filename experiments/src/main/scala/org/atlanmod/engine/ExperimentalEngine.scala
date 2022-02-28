package org.atlanmod.engine

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.TimeResult
import org.atlanmod.tl.engine.{Parameters, TransformationEngine}
import org.atlanmod.tl.model.impl.DefaultModel
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}

import scala.reflect.ClassTag

object ExperimentalEngine {

    def executeWithConfig[SME : ClassTag, SML, SMC, SMR, TME : ClassTag, TML: ClassTag]
    (trans: Transformation[SME, SML, SMC, TME, TML], source: Model[SME, SML], metamodel: Metamodel[SME, SML, SMC, SMR],
     config: Parameters.Config): (DefaultModel[TME, TML], TimeResult) = {
        execute(trans, source, metamodel, config.configEngine, config._npartitions, config._sparkcontext)
    }

    def execute[SME : ClassTag, SML, SMC, SMR, TME : ClassTag, TML: ClassTag]
    (trans: Transformation[SME, SML, SMC, TME, TML], source: Model[SME, SML], metamodel: Metamodel[SME, SML, SMC, SMR],
     config: Parameters.ConfigEngine, npartition: Int, sc: SparkContext): (DefaultModel[TME, TML], TimeResult) = {

        val time_result = new TimeResult()

        // 0 - Share source model, metamodel and transformation
        time_result.start_broadcast()
        val tr = TransformationEngine.bcast(trans, config, sc)
        val sm = TransformationEngine.bcast(source, config, sc)
        val mm = TransformationEngine.bcast(metamodel, config, sc)
        time_result.end_broadcast()

        // 1 - Create tuples
        time_result.start_tuples()
        val tuples: RDD[List[SME]] = TransformationEngine.tuple_phase(config, tr, sm, mm, npartition, sc)
        time_result.end_tuples()

        // 2 - Instantiate phase
        time_result.start_instantiate()
        val tracelinks: TraceLinks[SME, TME] = TransformationEngine.instantiate_phase(config, tuples, tr, sm, mm, sc)
        time_result.end_instantiate()

        // 3 - Output element + source for apply
        time_result.start_extract()
        val (elements, sps): (Iterable[TME], Seq[Any]) = TransformationEngine.extract_phase(tracelinks)
        time_result.end_extract()

        // 4 - Broadcast tracelinks
        time_result.start_broadcast()
        val tls = TransformationEngine.bcast(tracelinks, config, sc)
        time_result.end_broadcast()

        // 5 - Apply phase
        time_result.start_apply()
        val links: Iterable[TML] = TransformationEngine.apply_phase(config, tls, tr, sm, mm, sps, npartition, sc)
        time_result.end_apply()

        (new DefaultModel[TME, TML](elements, links), time_result)
    }


}
