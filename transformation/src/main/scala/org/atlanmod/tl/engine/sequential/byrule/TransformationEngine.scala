package org.atlanmod.tl.engine.sequential.byrule

import org.atlanmod.tl.engine.{Apply, Instantiate}
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.TupleUtils

object TransformationEngine {
    // https://github.com/atlanmod/CoqTL/blob/simple/fr.inria.atlanmod.coqtl.coq/core/byrule/ByRuleSemantics.v

    def allModelElementsOfType[SME, SML, SMC, SMR](t: SMC, sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[SME] =
        sm.allModelElements.filter(e => mm.hasType(t, e))


    def allModelElementsOfTypes[SME, SML, SMC, SMR](lt: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        lt.map(t => allModelElementsOfType(t, sm, mm))


    def allTuplesOfTypes[SME, SML, SMC, SMR](l: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR]) : List[List[SME]] =
        allModelElementsOfTypes(l, sm, mm).foldRight(List(List()): List[List[SME]])((s1: List[SME], s2: List[List[SME]]) => TupleUtils.prod_cons(s1, s2))


    def allTuplesByRule[SME, SML, SMC, SMR, TME, TML, TMC](tr: Transformation[SME, SML, SMC, TME, TML],
                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        tr.getRules.flatMap(r => allTuplesOfTypes(r.getInTypes, sm, mm))


    def execute[SME, SML, SMC, SMR, TME, TML, TMC](tr: Transformation[SME, SML, SMC, TME, TML],
                                                    sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : Model[TME, TML] = {
        val tuples = allTuplesByRule(tr, sm, mm)
        /* Instantiate */ val elements = tuples.flatMap(t => Instantiate.instantiatePattern(tr, sm, mm, t))
        /* Apply */ val links = tuples.flatMap(t => Apply.applyPattern(tr, sm, mm, t))

        class tupleTModel(elements: List[TME], links: List[TML]) extends Model[TME, TML] {
            override def allModelElements: List[TME] = elements

            override def allModelLinks: List[TML] = links
        }
        new tupleTModel(elements, links)
    }
}
