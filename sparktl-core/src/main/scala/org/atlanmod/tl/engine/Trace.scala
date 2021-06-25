package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.atlanmod.tl.engine.Eval.evalIteratorExpr
import org.atlanmod.tl.engine.Instantiate.{instantiateElementOnPattern, matchPattern}
import org.atlanmod.tl.engine.Utils.{allTuples, allTuplesByRule, allTuplesParallel}
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
    : List[TraceLink[SME, TME]] = {
        matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPattern(r, sm, sp))
    }


    def trace[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                            sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : TraceLinks[SME, TME] = {
            new TraceLinksList(allTuples(tr, sm).flatMap(tuple => tracePattern(tr, sm, mm, tuple)))
    }

    def trace_HM[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                               sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : TraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        allTuples(tr, sm).foreach(tuple => {
            val tls = tracePattern(tr, sm, mm, tuple)
            if (tls.nonEmpty) tls_map.put(tuple, tls)
        })
        tls_map
    }

    def parallel_trace_HM[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                  sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                  npartition: Int, sc: SparkContext)
    : TraceLinks[SME, TME] = {
        val tls_map = new TraceLinksMap[SME, TME]()
        val t = allTuplesParallel(tr, sm, npartition, sc).collect
        t.foreach(tuple => {
            val tls = tracePattern(tr, sm, mm, tuple)
            if (tls.nonEmpty) tls_map.put(tuple, tls)
        })
        tls_map
    }

    def parallel_trace[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                               sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                               npartition: Int, sc: SparkContext)
    : TraceLinks[SME, TME] = {
        new TraceLinksList(allTuplesParallel(tr, sm, npartition, sc).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect)
    }


    def seq_trace_par_apply[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                               sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                               npartition: Int, sc: SparkContext)
    : TraceLinks[SME, TME] = {
        val tuples = sc.parallelize(allTuples(tr, sm), npartition)
        new TraceLinksList(tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect)
    }

    def seq_trace_par_apply_ByRule[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                           npartition: Int, sc: SparkContext)
    : TraceLinks[SME, TME] = {
        val tuples = sc.parallelize(allTuplesByRule(tr, sm, mm), npartition)
        new TraceLinksList(tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect)
    }


    def seq_trace_par_apply_ByRule_experiment[SME: ClassTag, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                                    sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                                    npartition: Int, sc: SparkContext)
    : TraceLinks[SME, TME] = {
        val tuples = allTuplesByRule(tr, sm, mm)
        val tuples_rdd = sc.parallelize(tuples, npartition)

        val t1 = System.nanoTime()
        val res = new TraceLinksList(tuples_rdd.flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect)
        val t2 = System.nanoTime()
        val time = (t1 - t2) * 1000 / 1e9d

        println("It took " + time + "ms to do the instantiate part")
        res
    }

}
