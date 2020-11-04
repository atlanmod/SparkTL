package org.atlanmod.tl.sequential

import org.atlanmod.tl.sequential.spec.{Model, Transformation}
import org.atlanmod.tl.util.TupleUtils

object Utils {
    def maxArity[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML] ): Int =
    tr.getRules.map (r => r.getInTypes).map (l => l.length).max

    def allModelElements[SME, SML] (sm: Model[SME, SML]): List[SME] =
    sm.allModelElements

    def allTuples[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML])
    : List[List[SME]] =
    TupleUtils.tuples_up_to_n (allModelElements (sm), maxArity (tr) )
}