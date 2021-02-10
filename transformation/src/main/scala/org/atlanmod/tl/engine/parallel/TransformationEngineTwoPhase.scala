package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
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

    private def instantiateTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.trace(tr, sm, mm)
        (tls.getTargetElements , tls)
    }


    def allSourcePatterns[SME, TME](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns.distinct

    private def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
        sc.parallelize(allSourcePatterns(tls)).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect().toList
    }

    override def execute[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML: ClassTag]
    (tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
     sc: SparkContext = null,
     makeModel: (List[TME], List[TML]) => Model[TME, TML] = (a, b) => ModelUtil.makeTupleModel[TME, TML](a, b))
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces(tr, sm, mm, sc)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls, sc)
        makeModel(elements, links)
    }

}
