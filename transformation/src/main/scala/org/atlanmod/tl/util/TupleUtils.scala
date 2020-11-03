package org.atlanmod.tl.tool

object TupleUtils {

    private def prod_cons[A](s1: List[A], s2: List[List[A]]): List[List[A]] =
        if (s1.isEmpty) List()
        else s2.map(l => s1.head :: l) ++ prod_cons(s1.tail, s2)

    private def tuples_of_length_n[A](s1: List[A], n : Int) : List[List[A]] =
        if (n == 0) List(List())
        else prod_cons(s1, tuples_of_length_n(s1, n-1))

    def tuples_up_to_n[A](s1: List[A], n : Int) : List[List[A]] =
        if (n == 0) tuples_of_length_n(s1, 0)
        else tuples_of_length_n(s1, 0)

}
