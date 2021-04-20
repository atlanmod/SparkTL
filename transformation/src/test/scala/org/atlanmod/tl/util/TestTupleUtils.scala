package org.atlanmod.tl.util

import org.scalatest.funsuite.AnyFunSuite

class TestTupleUtils extends AnyFunSuite {

test("tuples_up_to_n"){
    val list = (1 to 10).toList
    val sc = SparkUtils.context()
    val rdd = sc.parallelize(list)
    val max_arity = 3
    val res1 = TupleUtils.tuples_up_to_n_prime_parallel(rdd, max_arity)
    val res2 = TupleUtils.tuples_up_to_n_prime(list, max_arity)
    val res3 = TupleUtils.tuples_up_to_n_super(list, max_arity, sc)
    val expected = TupleUtils.tuples_up_to_n(list, max_arity)
    assert(ListUtils.eqList(res2, expected))
    assert(ListUtils.eqList(res1, expected))
    assert(ListUtils.eqList(res3, expected))
}

}
