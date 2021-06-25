package org.atlanmod.tl.util

import org.apache.spark.{SparkConf, SparkContext}

object SparkUtils {

    def context(ncore: Int = 0): SparkContext = {
//        val config = new SparkConf()
//        config.setAppName("Lab")
//        if (ncore == 0) config.setMaster("local") else config.setMaster("local[" + ncore + "]")
        val conf = new SparkConf()
        if(conf.getAll.size == 0) {
            if (ncore == 0) conf.setMaster("local")
            else conf.setMaster("local[" + ncore + "]")
            conf.set("spark.executor.memory", "6g")
            conf.setAppName("SparkTE with Local master")
        }
        val scontext : SparkContext = new SparkContext(conf)
        scontext.setLogLevel("OFF")
        scontext
    }

}