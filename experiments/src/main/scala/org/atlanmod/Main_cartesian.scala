package org.atlanmod

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.tl.util.TupleUtils

import scala.reflect.ClassTag

object Main_cartesian {
    final val DEFAULT_NCORE: Int = 1
    final val DEFAULT_NEXECUTOR: Int = 2
    final val DEFAULT_NPARTITION: Int = 1
    final val DEFAULT_SIZE: Int = 2000
    final val DEFAULT_ARITY: Int = 2

    var ncore: Int = DEFAULT_NCORE
    var nexecutor: Int = DEFAULT_NEXECUTOR
    var size: Int = DEFAULT_SIZE
    var npartition: Int = DEFAULT_NPARTITION
    var arity: Int = DEFAULT_ARITY

    def parseArgs(args: List[String]): Unit = {
        args match {
            case "-size" :: s :: args => {
                size = s.toInt
                parseArgs(args)
            }
            case "-core" :: core :: args => {
                ncore = core.toInt
                parseArgs(args)
            }
            case "-executor" :: executor :: args =>{
                nexecutor = executor.toInt
                parseArgs(args)
            }
            case "-arity" :: a :: args =>{
                arity = a.toInt
                parseArgs(args)
            }
            case _ :: args => parseArgs(args)
            case List() =>
        }
    }

    def tuples_to_n_tail_parallel[A: ClassTag](arity: Int, current_arity: Int, current: RDD[List[A]], empty:RDD[List[A]], values: RDD[A])
    : RDD[List[A]] = {
        current_arity match {
            case `arity` => current
            case n => tuples_to_n_tail_parallel(arity, n+1, values.cartesian(current).map(x => x._1 :: x._2).union(empty), empty, values)
        }
    }

   def tuples_to_n[A: ClassTag](arity: Int, default: RDD[List[A]], values: RDD[A])
   : RDD[List[A]] = {
       arity match {
           case 0 => default
           case n => default.union(values.cartesian(tuples_to_n(n-1, default, values)).map(x => x._1 :: x._2))
       }
   }

   def allTuple[A: ClassTag] (arity: Int, values: List[A], sc: SparkContext)
   : RDD[List[A]] = {
       val values_rdd: RDD[A] = sc.parallelize(values) // Values that are used, as an RDD for using `cartesian` function
       val default_rdd: RDD[List[A]] = sc.parallelize(List(List())) // Initial tuple: empty list
       tuples_to_n(arity, default_rdd, values_rdd) // List[RDD[List[A]]] -> RDD[List[A]]
       // tuples_to_n_tail_parallel(arity, 0, default_rdd, default_rdd, values_rdd)
   }

    def main(args: Array[String]): Unit = {
        val conf: SparkConf = new SparkConf()
        val sc: SparkContext = new SparkContext(conf)

        parseArgs(args.toList)

        val values: List[Int] = (1 to size).toList
        val t1 = System.currentTimeMillis()
        TupleUtils.tuples_up_to_n_parallel(values, arity, npartition, sc)
        val t2 = System.currentTimeMillis()

        val t3 = System.currentTimeMillis()
        allTuple(arity, values, sc).collect()
        val t4 = System.currentTimeMillis()

        val a_line = List(ncore, nexecutor, size, arity, t2 - t1, t4 - t3).mkString(",")
        println(a_line)
    }
}