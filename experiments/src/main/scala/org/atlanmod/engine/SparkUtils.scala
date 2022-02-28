package org.atlanmod.engine

import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.engine.ArgsUtils.getOrElse

object SparkUtils {

    def initSparkContext(nexecutor: Int = -1, ncores: Int = 1, executor_mem: String = "1g"): SparkContext = {
        val conf = new SparkConf()
        nexecutor match {
            case -1 => conf.setIfMissing("spark.master", "local")
            case _ => conf.setIfMissing("spark.master", "local[" + nexecutor + "]")
        }
        conf.setIfMissing("spark.app.name", "SparkTE")
        conf.setIfMissing("spark.executor.memory", executor_mem)
        conf.setIfMissing("spark.executor.cores", ncores.toString)
        SparkContext.getOrCreate(conf)
    }

    def getNPartition(args: Array[String], sc: SparkContext): Int = {
        val executors = getOrElse(args,
            "-executor",
            getOrElse(args, "-nexecutor", 1, a => a.toInt),
            a => a.toInt)
        val executor_cores = sc.getConf.get("spark.executor.cores").toInt
        executors * executor_cores * 4
    }

    def initSparkContextFromArgs(args: Array[String]): (SparkContext, Int) = {
        val sc = initSparkContext(getOrElse(args,
            "-executor",
            getOrElse(args, "-nexecutor", -1, a => a.toInt),
            a => a.toInt),
            getOrElse(args,
                "-core",
                getOrElse(args, "-ncore", 1, a => a.toInt),
                a => a.toInt),
            getOrElse(args,
                "-executor_memory",
                getOrElse(args, "-memory_executor", "1g", a => a),
                a => a)
        )
        val npartition = getOrElse(args,
            "-npartitions", getOrElse(args,
                "-npartition", getOrElse(args,
                    "-partitions", getOrElse(args,
                        "-partition", getNPartition(args, sc) ,
                        a=>a.toInt), a=>a.toInt), a=>a.toInt), a=>a.toInt)
        (sc, npartition)
    }


}
