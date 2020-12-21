package org.atlanmod.tl.util

object TupleUtils {

    def prod_cons[A](s1: List[A], s2: List[List[A]]): List[List[A]] =
        s1.flatMap(a => s2.map(x => a :: x))

    private def tuples_of_length_n[A](s1: List[A], n : Int) : List[List[A]] =
        n match {
            case 0 => List(List())
            case n1 => prod_cons(s1, tuples_of_length_n(s1, n1-1))
        }

    def tuples_up_to_n[A](s1: List[A], n : Int) : List[List[A]] =
        n match {
            case 0 => tuples_of_length_n(s1, 0)
            case n1 => tuples_of_length_n(s1, n1) ++ tuples_up_to_n(s1, n1-1)
        }

}
