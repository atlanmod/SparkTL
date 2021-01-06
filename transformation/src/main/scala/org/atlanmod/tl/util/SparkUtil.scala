package org.atlanmod.tl.util

import org.apache.spark.{SparkConf, SparkContext}

object SparkUtil {

    def context(ncore: Int = 0): SparkContext ={
        val config = new SparkConf()
        config.setAppName("Lab")
        if (ncore == 0) config.setMaster("local") else config.setMaster("local[" + ncore + "]")
        val scontext : SparkContext = new SparkContext(config)
        scontext.setLogLevel("OFF")
        scontext
    }

}