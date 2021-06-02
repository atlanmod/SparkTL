package org.atlanmod

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Main_wordcount {

    val DEFAULT_BIBLE = "~/bible.txt"
    var bible = DEFAULT_BIBLE
    val DEFAULT_CORE= "1"
    var core = DEFAULT_CORE
    val DEFAULT_WORKER = "1"
    var worker = DEFAULT_WORKER

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

    def foo(word: String, i: Int):(String, Int) = {
        my_sleep_milli(20, r.nextInt())
        (word, 1)
    }

    def wordcount(file: RDD[String]): Array[(String, Int)] = {
        val count =
            file.flatMap(line => line.split(" ")).
              map(word => foo(word, r.nextInt())).
              reduceByKey((a, b) => a + b).
              collect()
        count
    }

    def parseArgs(args: List[String]): Unit =
        args match {
            case "-path" :: (path: String) :: args => {
                bible = path
                parseArgs(args)
            }
            case "-core" :: (c: String) :: args => {
                core = c
                parseArgs(args)
            }
            case "-worker" :: (w: String) :: args =>{
                worker = w
                parseArgs(args)
            }
            case  _ :: (args: List[String]) => parseArgs(args)
            case List() =>
        }

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
        val sc = new SparkContext(conf)
        parseArgs(args.toList)
        val total_cores = worker.toInt * core.toInt
        val textFile = sc.textFile(bible, total_cores * 4)
        val t1 = System.nanoTime()
        wordcount(textFile)
        val t2 = System.nanoTime()
        val time = (t2 - t1) * 1000 / 1e9d
        println(worker +"," + core +","+ total_cores+"," + time)
    }
}