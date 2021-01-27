package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Eval.evalIteratorExpr
import org.atlanmod.tl.engine.Instantiate.{instantiateElementOnPattern, matchPattern}
import org.atlanmod.tl.engine.Utils.{allTuples, allTuplesByRule}
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl._
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

import scala.reflect.ClassTag

object Trace {

    private def traceElementOnPattern[SME, SML, TME, TML](o: OutputPatternElement[SME, SML, TME, TML],
                                                          sm: Model[SME, SML], sp: List[SME], iter: Int)
    : Option[TraceLink[SME, TME]] =
        instantiateElementOnPattern(o, sm, sp, iter) match {
            case Some(e) => Some(new TraceLinkImpl((sp, iter, o.getName), e))
            case None => None
        }


    private def traceIterationOnPattern[SME, SML, SMC, TME, TML](r:Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                                 sp: List[SME], iter: Int)
    : List[TraceLink[SME, TME]] =
        r.getOutputPatternElements.flatMap(o => optionToList(traceElementOnPattern(o, sm, sp, iter)))


    private def traceRuleOnPattern[SME, SML, SMC, TME, TML](r:Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                            sp: List[SME])
    : List[TraceLink[SME, TME]] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => traceIterationOnPattern(r, sm, sp, i))


    def tracePattern[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                           sp: List[SME])
    : List[TraceLink[SME, TME]] =
        matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPattern(r, sm, sp))


    def trace[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                            sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : TraceLinks[SME, TME] = {
        val tuples = allTuples(tr, sm)
        new TraceLinksList(tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple)))
    }

    def trace[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : TraceLinks[SME, TME] =
        new TraceLinksList(sc.parallelize(allTuplesByRule(tr, sm, mm)).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect().toList)

    def trace_par[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                            mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : ParallelTraceLinks[SME, TME] = {
        new TraceLinksListPar(
            allTuplesByRule(tr, sm, mm).flatMap(tuple => tracePattern(tr, sm, mm, tuple)), sc
        )
    }

    def trace_par_bis[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : ParallelTraceLinks[SME, TME] = {
        new TraceLinksListPar(
            sc.parallelize(allTuplesByRule(tr, sm, mm)).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect().toList, sc
        )
    }

    def traceHM[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : TraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        allTuplesByRule(tr, sm, mm).foreach(tuple => tls_map.put(tuple, tracePattern(tr, sm, mm, tuple)))
        tls_map
    }

    def traceHM[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                              sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : TraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        sc.parallelize(allTuplesByRule(tr, sm, mm)).foreach(tuple => tls_map.put(tuple, tracePattern(tr, sm, mm, tuple)))
        tls_map
    }

    def traceHM_par[SME, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : ParallelTraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        allTuplesByRule(tr, sm, mm).foreach(tuple => tls_map.put(tuple, tracePattern(tr, sm, mm, tuple)))
        new TraceLinksMapPar[SME, TME](tls_map, sc)
    }

    def traceHM_par_bis[SME, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                    mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
    : ParallelTraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        sc.parallelize(allTuplesByRule(tr, sm, mm)).foreach(tuple => tls_map.put(tuple, tracePattern(tr, sm, mm, tuple)))
        new TraceLinksMapPar[SME, TME](tls_map, sc)
    }



//
//
//    def trace_par[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
//                                                                    mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
//    : ParallelTraceLinks[SME, TME] = {
//        new TraceLinksMapPar(
//            allTuples(tr, sm).flatMap(tuple => tracePattern(tr, sm, mm, tuple)), sc
//        )
//    }
//
//    def trace_par_bis[SME: ClassTag, SML, SMC, SMR, TME: ClassTag, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
//                                                                        mm: Metamodel[SME, SML, SMC, SMR], sc: SparkContext)
//    : ParallelTraceLinks[SME, TME] = {
//        new TraceLinksListPar(
//            sc.parallelize(allTuples(tr, sm)).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect().toList, sc
//        )
//    }
}
