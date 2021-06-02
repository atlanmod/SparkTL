package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.engine.{Apply, Trace, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

object TransformationEngineTwoPhaseHM extends TransformationEngine {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */

    private def instantiateTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML],  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],npartition: Int, sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.parallel_trace_HM(tr, sm, mm, npartition, sc)
        (tls.getTargetElements , tls)
    }


    def allSourcePatternsParallel[SME, TME](tls: TraceLinks[SME, TME], sc:SparkContext) : RDD[List[SME]] =
        sc.parallelize(tls.getSourcePatterns)

    def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                      sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                      tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
        val tls_rdd = sc.parallelize(tls.getSourcePatterns)
        val tls_broad = sc.broadcast(tls)
        tls_rdd.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls_broad.value)).collect.toList
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext = null,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces(tr, sm, mm, npartition, sc)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls, sc)
        makeModel(elements, links)
    }


    def execute_test[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext = null,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : (TraceLinks[SME, TME], Model[TME, TML]) = {
        val elements_and_tls = instantiateTraces(tr, sm, mm, npartition, sc)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls, sc)
        (tls, makeModel(elements, links))
    }

}
