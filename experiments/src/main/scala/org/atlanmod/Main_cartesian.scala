package org.atlanmod

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

object Main_cartesian {

   def tuples_to_n[A: ClassTag](arity: Int, default: RDD[List[A]], values: RDD[A])
   : RDD[List[A]] = {
       arity match {
           case 0 => default
           case n => {
               val n_minus_1: RDD[List[A]] = tuples_to_n(n-1, default, values) // tuples of size n-1
               val current = values.cartesian(n_minus_1).map(x => x._1 :: x._2)
               println(current.collect().mkString(","))
               current.union(values.map(x => List{x}))
           }
       }
   }

   def allTuple[A: ClassTag] (arity: Int, values: List[A], sc: SparkContext)
   : RDD[List[A]] = {
       val values_rdd: RDD[A] = sc.parallelize(values) // Values that are used, as an RDD for using `cartesian` function
       val current_rdd: RDD[List[A]] = sc.parallelize(List()) // Initial tuple: empty list
       tuples_to_n(arity, current_rdd, values_rdd).union(sc.parallelize(List(List()))) // List[RDD[List[A]]] -> RDD[List[A]]
   }

    def main(args: Array[String]): Unit = {
        val conf: SparkConf = new SparkConf()
        conf.setAppName("Test allTuples")
        conf.setMaster("local[2]")
        val sc: SparkContext = new SparkContext(conf)
        val values: List[Int] = List(1,2)
        val result: RDD[List[Int]] = allTuple(3, values, sc) // List[Int] is representing a generated tuple
        println(result.collect().mkString(",")) // collect is used to get a sequential type
    }
}