package org.atlanmod.tl.engine

import org.atlanmod.tl.engine.Eval.{evalIteratorExpr, evalOutputPatternElementExpr, evalOutputPatternLinkExpr}
import org.atlanmod.tl.engine.Instantiate.matchPattern
import org.atlanmod.tl.model._
import org.atlanmod.tl.util.ArithUtils.indexes
import org.atlanmod.tl.util.ListUtils.optionToList

object Apply{

    def applyReferenceOnPatternTraces[SME,SML,SMC,TME,TML,STL,TTL](oper: OutputPatternElementReference[SME, SML, TME, TML, STL, TTL],
                                                                           tr: Transformation[SME, SML, SMC, TME, TML, STL, TTL],
                                                                           sm: Model[SME, SML],
                                                                           sp: List[SME], iter: Int, te: TME,
                                                                           tls: TraceLinks[STL, TTL])
    : Option[TML] = {
        evalOutputPatternLinkExpr(sm, sp, te, iter, tls, oper)
    }

    def applyElementOnPatternTraces[SME,SML,SMC,TME,TML,STL,TTL](ope: OutputPatternElement[SME, SML, TME, TML, STL, TTL],
                                                                          tr: Transformation[SME, SML, SMC, TME, TML, STL, TTL],
                                                                          sm: Model[SME, SML],
                                                                          sp: List[SME], iter: Int,
                                                                          tls: TraceLinks[STL, TTL])
    : List[TML] =
        ope.getOutputElementReferences.flatMap(oper =>
            evalOutputPatternElementExpr(sm, sp, iter, ope) match {
                case Some(l) => optionToList(applyReferenceOnPatternTraces(oper, tr, sm, sp, iter, l, tls))
                case None => List()
            }
        )

    def applyIterationOnPatternTraces[SME,SML,SMC,TME,TML,STL,TTL](r: Rule[SME, SML, SMC, TME, TML, STL, TTL],
                                                               tr: Transformation[SME, SML, SMC, TME, TML, STL, TTL],
                                                               sm: Model[SME, SML], sp: List[SME], iter: Int,
                                                               tls: TraceLinks[STL, TTL])
    : List[TML] =
        r.getOutputPatternElements.flatMap(o => applyElementOnPatternTraces(o, tr, sm, sp, iter, tls))

    def applyRuleOnPatternTraces[SME,SML,SMC,TME,TML,STL,TTL](r: Rule[SME,SML,SMC,TME,TML,STL,TTL],
                                                          tr: Transformation[SME,SML,SMC,TME,TML,STL,TTL],
                                                          sm: Model[SME,SML], sp: List[SME],
                                                          tls: TraceLinks[STL,TTL])
    : List[TML] =
        indexes(evalIteratorExpr(r, sm, sp)).flatMap(i => applyIterationOnPatternTraces(r, tr, sm, sp, i, tls))

    def applyPatternTraces[ID,SME,SML,SMC,SMR,TME,TML,STL,TTL](tr: Transformation[SME,SML,SMC,TME,TML,STL,TTL],
                                                         sm: Model[SME,SML], mm: Metamodel[ID,SME,SML,SMC,SMR],
                                                         sp: List[SME],  tls: TraceLinks[STL,TTL])
    : List[TML] =
        matchPattern(tr, sm, mm, sp).flatMap(r => applyRuleOnPatternTraces(r, tr, sm, sp, tls))


//    ------

    def findRule[SME,SML,SMC,TME,TML,STL,TTL](tr: Transformation[SME,SML,SMC,TME,TML,STL,TTL], rulename: String):
    Option[Rule[SME,SML,SMC,TME,TML,STL,TTL]] = tr.getRules.find(r => rulename.equals(r.getName))

    def applyPatternTracesWithRulename[ID,SME,SML,SMC,SMR,TME,TML, STL, TTL](tr: Transformation[SME, SML, SMC, TME, TML, STL, TTL],
                                                                sm: Model[SME, SML], mm: Metamodel[ID, SME,SML,SMC,SMR],
                                                                sp: List[SME], rulename: String,
                                                                tls: TraceLinks[STL, TTL])
    : List[TML] = {
        findRule(tr, rulename) match {
            case Some(rule) => applyRuleOnPatternTraces(rule, tr, sm, sp, tls)
            case _ => applyPatternTraces(tr, sm, mm, sp, tls)
        }
    }

    def applyPatternTracesWithRulename[ID,SME,SML,SMC,SMR,TME,TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML, STL, TTL],
                                                                sm: Model[SME,SML], mm: Metamodel[ID,SME,SML,SMC,SMR],
                                                                sp: (List[SME], String), tls: TraceLinks[STL,TTL])
    : List[TML] = {
        findRule(tr, sp._2) match {
            case Some(rule) => applyRuleOnPatternTraces(rule, tr, sm, sp._1, tls)
            case _ => applyPatternTraces(tr, sm, mm, sp._1, tls)
        }
    }


}
