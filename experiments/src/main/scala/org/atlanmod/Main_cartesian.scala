package org.atlanmod

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

object Main_cartesian {

    def parallel_prod_cons[A: ClassTag](arity: Int, rdd: RDD[List[A]], values: RDD[A])
    : (RDD[List[A]], List[RDD[List[A]]]) = {
        arity match {
            case 0 => (rdd, List(rdd))
            case n => {
                val n_1: (RDD[List[A]], List[RDD[List[A]]]) = parallel_prod_cons(n-1, rdd, values) // tuples of size n-1
                val current = values.cartesian(n_1._1).map(x => x._1 :: x._2)
                (current, current :: n_1._2)
            }
        }
    }

    def allTuple[A: ClassTag] (arity: Int, values: List[A], sc: SparkContext)
    : RDD[List[A]] = {
        val values_rdd: RDD[A] = sc.parallelize(values) // Values that are used, as an RDD for using `cartesian` function
        val current_rdd: RDD[List[A]] = sc.parallelize(List(List())) // Initial tuple: empty list
        parallel_prod_cons(arity, current_rdd, values_rdd)._2.reduce(_ union _) // List[RDD[List[A]]] -> RDD[List[A]]
    }

    def main(args: Array[String]): Unit = {
        val conf: SparkConf = new SparkConf()
        conf.setAppName("Test allTuples")
        conf.setMaster("local[2]")
        val sc: SparkContext = new SparkContext(conf)
        val values: List[Int] = List(1,2,3,4,5)
        val result: RDD[List[Int]] = allTuple(2, values, sc) // List[Int] is representing a generated tuple
        println(result.collect().mkString(",")) // collect is used to get a sequential type
    }
}