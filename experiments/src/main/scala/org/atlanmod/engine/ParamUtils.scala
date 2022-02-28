package org.atlanmod.engine

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel
import org.atlanmod.tl.engine.Parameters
import org.atlanmod.engine.ArgsUtils.{getOrElse, has}
import org.atlanmod.tl.engine.Parameters.ConfigSpark

object ParamUtils {

    def getEngineConfig(args: Array[String]): Parameters.ConfigEngine = {
        val tuples: String = getOrElse(args, "-tuples", Parameters.BYRULE_DISTINCT, a => a)
        val tls: String = getOrElse(args, "-tls", Parameters.TLS_MAP, a => a)
        val tracerule: Boolean = has(args, "-tracerule")
        val collect: String = getOrElse(args, "-collect", Parameters.COLLECT, a => a)
        val bcast: String = getOrElse(args, "-bcast", Parameters.BROADCASTED_TLS, a => a)
        val distinctApply: String = getOrElse(args, "-distinctApply", Parameters.APPLY_DISTINCT, a => a)
        new Parameters.ConfigEngine(tuples, tls, tracerule, collect, bcast, distinctApply)
    }

    def getModelConfig(args: Array[String]): Parameters.ConfigModel = {
        val linkType: String = getOrElse(args, "-linkType", Parameters.LINKS_MAP, a => a)
        new Parameters.ConfigModel(linkType)
    }

    def getTransformationConfig(args: Array[String]): Parameters.ConfigTransformation = {
        val guard_sleep = getOrElse(args, "-sleepGuard", getOrElse(args, "-sleep", 0, a => a.toInt), a => a.toInt)
        val instantiate_sleep = getOrElse(args, "-sleepInstantiate", getOrElse(args, "-sleep", 0, a => a.toInt), a => a.toInt)
        val apply_sleep = getOrElse(args, "-sleepApply", getOrElse(args, "-sleep", 0, a => a.toInt), a => a.toInt)
        val memoization = has(args, "-memoization")
        new Parameters.ConfigTransformation(guard_sleep, instantiate_sleep, apply_sleep, memoization)
    }

    def getSparkConfig(args: Array[String]): Parameters.ConfigSpark = {
        val sp_conf = new SparkConf()
        val ncores: Int = sp_conf.get("spark.executor.cores", getOrElse(args, List("-cores","-ncores","-ncore","-core"), "1", a => a)).toInt
        var nexecutors: Int = getOrElse(args, List("-nexecutor","-nexecutors","-executor","-executors"), -1, a => a.toInt)
        val executor_memory: String = sp_conf.get("spark.executor.memory", getOrElse(args, List("-executor_memory","-executors_memory"), "1g", a => a))
        val sc : SparkContext = {
            if (nexecutors == -1) {
                sp_conf.setIfMissing("spark.master", "local")
                nexecutors = 1
            } else
                sp_conf.setIfMissing("spark.master", "local[" + nexecutors + "]")
            sp_conf.setIfMissing("spark.app.name", "SparkTE")
            sp_conf.setIfMissing("spark.executor.memory", executor_memory)
            sp_conf.setIfMissing("spark.executor.cores", ncores.toString)
            new SparkContext(sp_conf)
        }
        val npartitions: Int = ncores * nexecutors * 4
        val storage = StorageLevel.fromString(getOrElse(args, List("-storage","-storageLevel"), Parameters.STORAGE_MEMORY_AND_DISK, a => a))
        new ConfigSpark(sc, nexecutors, npartitions, executor_memory, storage)
    }

    def getConfig(args: Array[String]): Parameters.Config = {
        val confEngine = getEngineConfig(args)
        val confModel = getModelConfig(args)
        val confTransformation = getTransformationConfig(args)
        val confSpark = getSparkConfig(args)
        new Parameters.Config(confEngine, confModel, confTransformation, confSpark)
    }

}
