package org.atlanmod.tl.util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object TupleUtils {

    def prod_cons[A](s1: List[A], s2: List[List[A]]): List[List[A]] =
        s1.flatMap(a => s2.map(x => a :: x))

    private def tuples_of_length_n[A](s1: List[A], n : Int) : List[List[A]] = {
        n match {
            case 0 => List(List())
            case n1 => prod_cons(s1, tuples_of_length_n(s1, n1-1))
        }
    }

    def tuples_up_to_n[A](s1: List[A], n : Int) : List[List[A]] =
        n match {
            case 0 => tuples_of_length_n(s1, 0)
            case n1 => tuples_of_length_n(s1, n1) ++ tuples_up_to_n(s1, n1-1)
        }

    def prod_cons_parallel[A: ClassTag](s1: List[A], s2: List[List[A]], npartition: Int, sc: SparkContext): RDD[List[A]] =
        sc.parallelize(s1, npartition).flatMap(a => s2.map(x => a :: x))

    def tuples_up_to_n_parallel[A: ClassTag](s1: List[A], n : Int, npartition: Int, sc: SparkContext) : RDD[List[A]] =
       prod_cons_parallel( s1, tuples_up_to_n(s1, n-1), npartition, sc)

    def tuples_up_to_n_prime[A](s1: List[A], n : Int) : List[List[A]] =
        (0 to n).map(i => tuples_of_length_n(s1, i)).reduce((a, b) => a ++ b)

    def prod_cons[A](s1: RDD[A], s2: List[List[A]]): List[List[A]] =
        s1.flatMap(a => s2.map(x => a :: x)).collect.toList

    private def tuples_of_length_n[A](s1: RDD[A], n : Int) : List[List[A]] =
        n match {
            case 0 => List(List())
            case n1 => prod_cons(s1, tuples_of_length_n(s1, n1-1))
        }

    def tuples_up_to_n_prime_parallel[A](s1: RDD[A], n : Int) : List[List[A]] =
        (0 to n).map(i => tuples_of_length_n(s1, i)).reduce((a, b) => a ++ b)

}
