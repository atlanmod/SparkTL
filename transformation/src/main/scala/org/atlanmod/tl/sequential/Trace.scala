package org.atlanmod.tl.sequential

import org.atlanmod.tl.sequential.Eval.evalIteratorExpr
import org.atlanmod.tl.sequential.Instantiate.{instantiateElementOnPattern, matchPattern}
import org.atlanmod.tl.sequential.Utils.allTuples
import org.atlanmod.tl.model.impl.TraceLinkImpl
import org.atlanmod.tl.model.{Metamodel, Model, OutputPatternElement, Rule, TraceLink, Transformation}
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

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


    private def tracePattern[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR],
                                                           sp: List[SME])
    : List[TraceLink[SME, TME]] =
        matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPattern(r, sm, sp))


    def trace[SME, SML, SMC, SMR, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML],
                                            sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[TraceLink[SME, TME]] =
      allTuples(tr, sm).flatMap(tuple => tracePattern(tr, sm, mm, tuple))

}
