package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.atlanmod.tl.engine.{Apply, Trace, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks, Transformation}
import org.atlanmod.tl.util.ModelUtil

import scala.reflect.ClassTag

object TransformationEngineTwoPhase extends TransformationEngine {
    /*
     *  SME : SourceModelElement
     *  SML : SourceModelLink
     *  SMC : SourceModelClass
     *  TME : TargetModelElement
     *  TML : TargetModelLink
     *  TMC : TargetModelClass
     */
    private def instantiateTraces_par[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML],  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], npartition: Int,
     sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.parallel_trace(tr, sm, mm, npartition, sc)
        (tls.getTargetElements , tls)
    }

    private def instantiateTraces_seq[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls = Trace.trace(tr, sm, mm)
        (tls.getTargetElements, tls)
    }

    def allSourcePatterns[SME: ClassTag, TME: ClassTag](tls: TraceLinks[SME, TME]) : List[List[SME]] = {
        tls.getSourcePatterns.distinct
    }


    private def applyTraces[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                              tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
        sc.parallelize(allSourcePatterns(tls)).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect().toList
    }


    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     npartition: Int, sc: SparkContext = null,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces_par(tr, sm, mm, npartition, sc)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val tls_broad: Broadcast[TraceLinks[SME, TME]] = sc.broadcast(tls)
        val links = applyTraces(tr, sm, mm, tls_broad.value, sc)
        makeModel(elements, links)
    }

}
