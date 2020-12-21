package org.atlanmod.tl.engine

import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.TupleUtils

object Utils {


    def allModelElementsOfType[SME, SML, SMC, SMR](t: SMC, sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[SME] =
        sm.allModelElements.filter(e => mm.hasType(t, e))


    def allModelElementsOfTypes[SME, SML, SMC, SMR](lt: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        lt.map(t => allModelElementsOfType(t, sm, mm))


    def allTuplesOfTypes[SME, SML, SMC, SMR](l: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        allModelElementsOfTypes(l, sm, mm).foldRight(List(List()): List[List[SME]])((s1: List[SME], s2: List[List[SME]]) => TupleUtils.prod_cons(s1, s2))


    def allTuplesByRule[SME, SML, SMC, SMR, TME, TML, TMC](tr: Transformation[SME, SML, SMC, TME, TML],
                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        tr.getRules.flatMap(r => allTuplesOfTypes(r.getInTypes, sm, mm))


    def maxArity[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML] ): Int =
    tr.getRules.map (r => r.getInTypes).map (l => l.length).max


    def allModelElements[SME, SML] (sm: Model[SME, SML]): List[SME] =
    sm.allModelElements


    def allTuples[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML])
    : List[List[SME]] =
    TupleUtils.tuples_up_to_n (allModelElements (sm), maxArity (tr) )

}