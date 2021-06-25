package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{Metamodel, Model, Transformation}
import org.atlanmod.tl.util.TupleUtils

import scala.reflect.ClassTag

object Utils {


    def allModelElementsOfType[SME, SML, SMC, SMR](t: SMC, sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[SME] =
        sm.allModelElements.filter(e => mm.hasType(t, e))


    def allModelElementsOfTypes[SME, SML, SMC, SMR](lt: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        lt.map(t => allModelElementsOfType(t, sm, mm))

    private def fold_right[A, B] (f: (B, A) => A, a0: A, l: List[B]): A = {
        l match {
            case b::t => f(b, fold_right(f, a0, t))
            case _ => a0
        }
    }

    def allTuplesOfTypes[SME, SML, SMC, SMR](l: List[SMC], sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
        fold_right(
            (a: List[SME], b: List[List[SME]]) => TupleUtils.prod_cons(a, b),
            List(List()),
            allModelElementsOfTypes(l, sm, mm)
        )

    def allTuplesByRule[SME, SML, SMC, SMR, TME, TML, TMC](tr: Transformation[SME, SML, SMC, TME, TML],
                                                           sm: Model[SME, SML], mm: Metamodel[SME, SML, SMC, SMR])
    : List[List[SME]] =
       tr.getRules.flatMap(r => allTuplesOfTypes(r.getInTypes, sm, mm))

    def maxArity[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML] ): Int =
        tr.getRules.map(r => r.getInTypes).map (l => l.length).max

    def allModelElements[SME, SML] (sm: Model[SME, SML]): List[SME] =
        sm.allModelElements

    def allTuples[SME, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML])
    : List[List[SME]] =
    TupleUtils.tuples_up_to_n_prime(allModelElements (sm), maxArity (tr) )

    def allTuplesParallel[SME: ClassTag, SML, SMC, TME, TML](tr: Transformation[SME, SML, SMC, TME, TML], sm: Model[SME, SML],
                                                             npartition:Int, sc: SparkContext)
    : RDD[List[SME]] =
        TupleUtils.tuples_up_to_n_parallel(allModelElements (sm), maxArity (tr), npartition, sc)

}