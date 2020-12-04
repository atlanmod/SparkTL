package org.atlanmod.tl.util

object TupleUtils {

    private def prod_cons[A](s1: List[A], s2: List[List[A]]): List[List[A]] =
        if (s1.isEmpty) List()
        else s2.map(l => s1.head :: l) ++ prod_cons(s1.tail, s2)

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
