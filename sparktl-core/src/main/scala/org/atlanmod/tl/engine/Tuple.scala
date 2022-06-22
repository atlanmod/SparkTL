package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.atlanmod.tl.model.{Metamodel, Model, Rule, Transformation}
import org.atlanmod.tl.util.TupleUtils

import scala.reflect.ClassTag

object Tuple {

    type Tuple[A] = List[A]

    private def RDDs_cartesian_product[A: ClassTag](rdds: Seq[RDD[A]]) : Option[RDD[List[A]]] = {
        rdds match {
            case List(h) => Some(h.map(a => List(a)))
            case h1 :: h2 :: t =>
                val res: RDD[List[A]] = h1.cartesian(h2).map(t => List(t._1, t._2))
                RDDs_cartesian_product(t) match {
                    case Some(rdd) => Some(res.cartesian(rdd).map(t => t._1 ++ t._2))
                    case None => Some(res)
                }
            case List() => None
        }
    }


    private def allTuplesOfType_VertexRDD[A, T](types: List[T], vertices: VertexRDD[A], metamodel: Metamodel[Any, A, Any, T, Any]): RDD[Tuple[(VertexId, A)]] = {
        RDDs_cartesian_product(types.map(t => vertices.filter(v => metamodel.hasType(t, v._2)))) match {
            case Some(e) => e
            case _ => throw new UnsupportedOperationException("Cannot make tuples from an empty transformation")
        }
    }

    private def allTuplesByRule_VertexRDD[A, T](r: Rule[A, Any, T, Any, Any, Any, Any], vertices: VertexRDD[A], metamodel: Metamodel[Any,A, Any, T, Any])
    : RDD[Tuple[(VertexId, A)]] = {
        allTuplesOfType_VertexRDD(r.getInTypes, vertices, metamodel)
    }

    def allTuplesByRules_Graph[A, B, T](g: Graph[A, B], t: Transformation[A, Any, T, Any, Any, Any, Any], metamodel: Metamodel[Any, A, Any, T, Any])
    : RDD[Tuple[(VertexId, A)]] = {
        t.getRules.map(r => allTuplesByRule_VertexRDD(r, g.vertices, metamodel)).reduce(_ union _)
    }

    def allTuplesByRules_Graph_distinct[A, B, T](g: Graph[A, B], t: Transformation[A, Any, T, Any, Any, Any, Any], metamodel: Metamodel[Any,A, Any, T, Any]):
    RDD[Tuple[(VertexId, A)]] = {
        t.getRules.map(r => r.getInTypes).distinct.map(types => allTuplesOfType_VertexRDD(types, g.vertices, metamodel)).reduce(_ union _)
    }

    def allTuples_Graph[A, B](g: Graph[A, B], t: Transformation[A, Any, Any, Any, Any, Any, Any], metamodel: Metamodel[Any, A, Any, Any, Any]):
    RDD[Tuple[(VertexId, A)]] = {
        RDDs_cartesian_product((1 to maxArity(t)).map(_ => g.vertices)) match {
            case Some(res) => res
            case _ => throw new UnsupportedOperationException("Cannot make tuples from an empty transformation")
        }
    }

//    --------------------------------------------------------------------------------------------

    def allModelElementsOfTypes[ID,SME, SML, SMC, SMR](lt: List[SMC], sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR])
    : List[List[SME]] =
        lt.map(t => mm.allModelElementsOfType(t, sm))

    private def fold_right[A, B] (f: (B, A) => A, a0: A, l: List[B]): A = {
        l match {
            case b::t => f(b, fold_right(f, a0, t))
            case _ => a0
        }
    }

    def allTuplesOfTypes[ID,SME, SML, SMC, SMR](l: List[SMC], sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR])
    : List[List[SME]] =
        fold_right(
            (a: List[SME], b: List[List[SME]]) => TupleUtils.prod_cons(a, b),
            List(List()),
            allModelElementsOfTypes(l, sm, mm)
        )

    def allTuplesByRule[ID,SME, SML, SMC, SMR, TME, TML,TMC,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL],
                                                           sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR])
    : List[List[SME]] =
       tr.getRules.flatMap(r => allTuplesOfTypes(r.getInTypes, sm, mm))


    def allTuplesByRuleDistinct[ID,SME, SML, SMC, SMR, TME, TML, TMC,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL],
                                                                   sm: Model[SME, SML], mm: Metamodel[ID,SME, SML, SMC, SMR])
    : List[List[SME]] =
        tr.getRules.map(r => r.getInTypes).distinct.flatMap(it => allTuplesOfTypes(it, sm, mm))

    def maxArity[SME, SML, SMC, TME, TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL] ): Int =
        tr.getRules.map(r => r.getInTypes).map (l => l.length).max

    def allModelElements[SME, SML, SMC] (sm: Model[SME, SML]): List[SME] =
        sm.allModelElements

    def allTuples[SME, SML, SMC, TME, TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL], sm: Model[SME, SML])
    : List[List[SME]] =
    TupleUtils.tuples_up_to_n_prime(allModelElements (sm), maxArity (tr) )

    def allTuplesParallel[SME: ClassTag, SML, SMC, TME, TML,STL,TTL](tr: Transformation[SME, SML, SMC, TME, TML,STL,TTL],
                                                                     sm: Model[SME, SML], npartition:Int, sc: SparkContext)
    : RDD[List[SME]] =
        TupleUtils.tuples_up_to_n_parallel(allModelElements (sm), maxArity (tr), npartition, sc)

}


