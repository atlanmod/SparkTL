package org.atlanmod

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main_sleep {

    val DEFAULT_CORE= "1"
    var core = DEFAULT_CORE
    val DEFAULT_WORKER = "1"
    var worker = DEFAULT_WORKER
    val DEFAULT_SIZE = 100
    var size = DEFAULT_SIZE

    val r = scala.util.Random

    def my_sleep(sec: Int, rd: Int): Int = {
        var d = rd.toDouble
        val end = System.nanoTime() + sec * 1e9
        var current = System.nanoTime
        while(current < end){
            current = System.nanoTime
            d += 1.0
        }
        d.toInt
    }

    def my_sleep_milli(m: Int, rd: Int): Int = {
        var d = rd.toDouble
        val end = System.nanoTime() + m * 1e6
        var current = System.nanoTime
        while(current < end){
            current = System.nanoTime
            d += 1.0
        }
        d.toInt
    }

    def foo(i: Int, rd: Int): Int = {
        my_sleep(1, r.nextInt())
        rd + i
    }

    def parseArgs(args: List[String]): Unit =
        args match {
            case "-core" :: (c: String) :: args => {
                core = c
                parseArgs(args)
            }
            case "-size" :: (s: String) :: args => {
                size = s.toInt
                parseArgs(args)
            }
            case "-worker" :: (w: String) :: args =>{
                worker = w
                parseArgs(args)
            }
            case  _ :: (args: List[String]) => parseArgs(args)
            case List() =>
        }

    def sleeping(rdd: RDD[Int]): Array[Int] = {
        rdd.map(k => foo(k, r.nextInt())).collect()
    }

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
        conf.setMaster("local[1]")
        conf.setAppName("test")
        val sc = new SparkContext(conf)
        parseArgs(args.toList)
        val total_cores = worker.toInt * core.toInt
        val rdd = sc.parallelize(1 to size, total_cores * 4)
        val t1 = System.nanoTime()
        sleeping(rdd)
        val t2 = System.nanoTime()
        val time = (t2 - t1) * 1000 / 1e9d
        println(worker +"," + core +","+ total_cores+"," + time)
    }

}