package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Utils.allTuples
import org.atlanmod.tl.engine.{Apply, Trace, TransformationEngine}
import org.atlanmod.tl.model.{Metamodel, Model, TraceLink, Transformation}

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
//
//    override def execute[SME, SML, SMC, SMR, TME : ClassTag , TML : ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
//                                                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
//                                                                              sc: SparkContext)
//    : Model[TME, TML] = {
//        val tuples : RDD[List[SME]] = sc.parallelize(allTuples(tr, sm))
//        /* Instantiate */ val elements : RDD[TME] = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
//        /* Apply */ val links : RDD[TML] = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))
//
//        class tupleTModel(elements: RDD[TME], links: RDD[TML]) extends Model[TME, TML] {
//            override def allModelElements: List[TME] = elements.collect.toList
//            override def allModelLinks: List[TML] = links.collect.toList
//        }
//
//        new tupleTModel(elements, links)
//    }

    private def instantiateTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                sc: SparkContext)
    : (List[TME], List[TraceLink[SME, TME]]) = {
        val tls = Trace.trace(tr, sm, mm)
        val para_tls = sc.parallelize(tls)
        (para_tls.map(tl => tl.getTargetElement).collect().toList, tls)
    }

    private def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: List[TraceLink[SME, TME]], sc: SparkContext)
    : List[TML] = {
        sc.parallelize(allTuples(tr, sm)).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect().toList
    }

    override def execute[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           sc: SparkContext)
    : Model[TME, TML] = {
        val elements_and_tls = instantiateTraces(tr, sm, mm, sc)
        val elements = elements_and_tls._1
        val tls = elements_and_tls._2
        val links = applyTraces(tr, sm, mm, tls, sc)
        class tupleTModel(elements: List[TME], links: List[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements
            override def allModelLinks: List[TML] = links
        }
        new tupleTModel(elements, links)
    }

}
