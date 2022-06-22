package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Eval.evalIteratorExpr
import org.atlanmod.tl.engine.Instantiate.{instantiateElementOnPattern, matchPattern}
import org.atlanmod.tl.model._
import org.atlanmod.tl.model.impl._
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

object Trace {
    
    private def traceElementOnPattern[SME, SML, SMC, TME, TML, STL, TTL](o: OutputPatternElement[SME, SML, TME, TML, STL, TTL],
                                                          sm: Model[SME, SML], sp: List[SME], iter: Int)
    : Option[TraceLink[SME, TME]] =
        instantiateElementOnPattern(o, sm, sp, iter) match {
            case Some(e) => Some(new TraceLinkImpl((sp, iter, o.getName), e))
            case None => None
        }

    private def traceIterationOnPattern[SME, SML, SMC, TME, TML, STL, TTL](r:Rule[SME, SML, SMC, TME, TML, STL, TTL],
                                                                           sm: Model[SME, SML], sp: List[SME], iter: Int)
    : List[TraceLink[SME, TME]] =
        r.getOutputPatternElements.flatMap(o => optionToList(traceElementOnPattern(o, sm, sp, iter)))

    private def traceRuleOnPattern[SME, SML, SMC, TME, TML, STL, TTL](r:Rule[SME, SML, SMC, TME, TML,STL,TTL],
                                                                      sm: Model[SME, SML], sp: List[SME])
    : List[TraceLink[SME, TME]] = {
        val idx = indexes(evalIteratorExpr(r, sm, sp))
        val res = idx.flatMap(i => traceIterationOnPattern(r, sm, sp, i))
        res
    }


    def tracePattern[ID,SME, SML, SMC, SMR, TME, TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL],
                                                           sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR],
                                                           sp: List[SME])
    : List[TraceLink[SME, TME]] = {
        matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPattern(r, sm, sp))
    }

    // --------------------------

    private def traceElementOnPatternWithRule[SME, SML, SMC, TME, TML,STL,TTL](o: OutputPatternElement[SME, SML, TME, TML,STL,TTL],
                                                                       rule: String,
                                                                       sm: Model[SME, SML], sp: List[SME], iter: Int)
    : Option[TraceLink[SME, TME]] =
        instantiateElementOnPattern(o, sm, sp, iter) match {
            case Some(e) => Some(new TraceLinkWithRuleImpl((sp, iter, rule, o.getName), e))
            case None => None
        }

    private def traceIterationOnPatternWithRule[SME, SML, SMC, TME, TML,STL,TTL](r:Rule[SME, SML, SMC, TME, TML,STL,TTL], sm: Model[SME, SML],
                                                                         sp: List[SME], iter: Int)
    : List[TraceLink[SME, TME]] =
        r.getOutputPatternElements.flatMap(o => optionToList(traceElementOnPatternWithRule(o, r.getName, sm, sp, iter)))

    private def traceRuleOnPatternWithRule[SME, SML, SMC, TME, TML,STL,TTL](r:Rule[SME, SML, SMC, TME, TML,STL,TTL], sm: Model[SME, SML],
                                                                    sp: List[SME])
    : List[TraceLink[SME, TME]] = {
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => traceIterationOnPatternWithRule(r, sm, sp, i))
    }

    def tracePatternWithRule[ID,SME, SML, SMC, SMR, TME, TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL],
                                                   sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR],
                                                   sp: List[SME])
    : List[TraceLink[SME, TME]] = {
        matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPatternWithRule(r, sm, sp))
    }

}
