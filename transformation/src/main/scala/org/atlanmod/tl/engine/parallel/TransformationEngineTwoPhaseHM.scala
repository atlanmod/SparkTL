package org.atlanmod.tl.engine.parallel

import org.apache.spark.SparkContext
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

    private def instantiateTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                sc: SparkContext)
    : (List[TME], TraceLinks[SME, TME]) = {
        val tls : TraceLinks[SME, TME] = Trace.trace_HM(tr, sm, mm)
        (tls.getTargetElements , tls)
    }


    def allSourcePatterns[SME, TME](tls: TraceLinks[SME, TME]) : List[List[SME]] =
        tls.getSourcePatterns

    def eq_lsme[SME](l1: List[SME], l2: List[SME]) : Boolean = {
        for (sme1 <- l1) {
            if(l1.count(v => v.equals(sme1)) != l2.count(v => v.equals(sme1))) return false
        }
        for (sme <- l2) {
            if(l1.count(v => v.equals(sme)) != l2.count(v => v.equals(sme))) return false
        }
        true
    }

    def eq_llsme[SME](l1: List[List[SME]], l2: List[List[SME]]) : Boolean = {
        for (lsme1 <- l1) {
            if(l1.count(lsme => lsme.equals(lsme1)) != l2.count(lsme => lsme.equals(lsme1))) return false
//            if(l1.count(lsme => eq_lsme(lsme, lsme1)) != l2.count(lsme => eq_lsme(lsme, lse1))) return false
        }
        for (lsme1 <- l2) {
            if(l1.count(lsme => eq_lsme(lsme, lsme1)) != l2.count(lsme => eq_lsme(lsme, lsme1))) return false
        }
        true
    }

    private def applyTraces[SME, SML, SMC, SMR, TME: ClassTag, TML: ClassTag](tr: Transformation[SME, SML, SMC, TME, TML],
                                                          sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                          tls: TraceLinks[SME, TME], sc: SparkContext)
    : List[TML] = {
//        val sp1 = allSourcePatterns(tls).toArray
//        val sp2 = sc.parallelize(allSourcePatterns(tls)).collect()
////        sc.parallelize(allSourcePatterns(tls)).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls)).collect().toList
////        sc.parallelize(allSourcePatterns(tls)).collect().toList.flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))
//        for(i <- 0 to sp1.length) {
//            val r1 = Apply.applyPatternTraces(tr, sm, mm, sp1(i), tls)
//            val r2 = Apply.applyPatternTraces(tr, sm, mm, sp2(i), tls)
//            val r = r1.equals(r2)
//            if(!r) {
//                val op1 = matchPattern(tr, sm, mm, sp1(i))(0).getOutputPatternElements(0)
//                val op2 = matchPattern(tr, sm, mm, sp2(i))(0).getOutputPatternElements(0)
//                val oper1 = op1.getOutputElementReferences(0)
//                val oper2 = op2.getOutputElementReferences(0)
//                val opers = (oper1, oper2)
//                val e1 = evalOutputPatternElementExpr(sm, sp1(i), 0, op1)
//                val e2 = evalOutputPatternElementExpr(sm, sp2(i), 0, op2)
//                val es = (e1, e2)
//                e1 match {
//                    case Some(l) =>
//                        print("")
//                    case _ => print("")
////                   case Some(l) =>
////                       val v = l
////                   case _ => _
//                }
//            }
//        }

        allSourcePatterns(tls).flatMap(sp => Apply.applyPatternTraces(tr, sm, mm, sp, tls))
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
