package org.atlanmod.tl.util

import org.apache.spark.{SparkConf, SparkContext}

object SparkUtil {

    private var scontext : SparkContext = null
    private var sconf : SparkConf = null


    def config: SparkConf = {
        if (sconf == null) {
            sconf = new SparkConf()
            sconf.setAppName("Lab")
            sconf.setMaster("local")
        }
        sconf
    }

    def context: SparkContext ={
        if (scontext == null) {
            scontext = new SparkContext(config)
            scontext.setLogLevel("OFF")
        }
        scontext
    }
}