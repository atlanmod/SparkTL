package org.atlanmod.tl.engine

import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

object Parameters {

    // Tracelinks structure := HashMap | List |
    final val TLS_MAP = "map"
    final val TLS_LIST = "list"
    final val TLS_ARRAY = "array"
    final val TLS_RDD = "rdd"
    final val TLS_DF = "df"

    // Tuples generation
    final val BYRULE = "byrule"
    final val BYRULE_DISTINCT = "byrule_distinct"
    final val BYRULE_UNIQUE = "byrule_unique"
    final val FULL = "full"

    // Tuples generation
    final val APPLY_SIMPLE = "apply"
    final val APPLY_DISTINCT = "apply_distinct"

    // Tracelinks bcast
    final val BROADCASTED_TLS = "bcast"
    final val SHARED_TLS = "shared"

    // Tracelinks content
    final val WITH_RULE = true
    final val WITHOUT_RULE = false

    // Collection method
    final val FOLD = "fold"
    final val COLLECT = "collect"

    // Pipeline
    final val tbd = "tbd"

    class ConfigEngine {

        var tuples: String = ""
        var tls: String = ""
        var tracerule: Boolean = false
        var collect: String = ""
        var bcast: String = ""
        var distinctApply: String = ""
        var withId: Boolean = false
        var parallelExtract: Boolean = false

        def this(tuples: String = BYRULE_UNIQUE, tls: String = TLS_MAP, tracerule: Boolean = WITHOUT_RULE,
                 collect: String = COLLECT, bcast: String = BROADCASTED_TLS, distinctApply: String = APPLY_DISTINCT,
                 withId: Boolean = false, parallelExtract: Boolean){
            this()
            this.tuples = tuples
            this.tls = tls
            this.tracerule = tracerule
            this.collect = collect
            this.bcast = bcast
            this.distinctApply = distinctApply
            this.withId = withId
            this.parallelExtract = parallelExtract
        }

        def _tuples: String = tuples
        def _tls: String = tls
        def _tracerule: Boolean = tracerule
        def _collect: String = collect
        def _bcast: String = bcast
        def _distinctApply: String = distinctApply
        def _withId: Boolean = withId
        def _parallelExtract: Boolean = parallelExtract

        def set_tuples(tuples: String): Unit = this.tuples = tuples
        def set_tls(tls: String): Unit = this.tls = tls
        def set_tracerule(tracerule: Boolean): Unit = this.tracerule = tracerule
        def set_withId(withId: Boolean): Unit = this.withId = withId
        def set_collect(collect: String): Unit = this.collect = collect
        def set_bcast(bcast: String): Unit = this.bcast = bcast
        def set_distinctApply(distinctApply: String): Unit = this.distinctApply = distinctApply
        def set_parallelExtract(parallelExtract: Boolean): Unit = this.parallelExtract = parallelExtract

    }

    // Model links
    final val LINKS_LIST = "list"
    final val LINKS_MAP = "map"

    // Model type
    final val DYNAMIC_MODEL = "dynamic"
    final val GRAPHX_MODEL = "graphx"

    class ConfigModel {

        var linkType: String = ""
        var modelType: String = ""

        def this(modelType: String = DYNAMIC_MODEL , linkType: String = LINKS_MAP){
            this()
            this.modelType = modelType
            this.linkType = linkType
        }

        def _link_type: String = linkType

        def set_linkType (linkType: String): Unit = this.linkType = linkType
    }

    // Memoization
    final val WITH_MEMOIZATION = true
    final val NO_MEMOIZATION = false

    class ConfigTransformation {

        var sleepGuard: Int = 0
        var sleepInstantiate: Int = 0
        var sleepApply: Int = 0
        var memoization: Boolean = NO_MEMOIZATION


        def this(sleepGuard: Int, sleepInstantiate: Int, sleepApply: Int) = {
            this()
            this.sleepGuard = sleepGuard
            this.sleepInstantiate = sleepInstantiate
            this.sleepApply = sleepApply
        }

        def this(sleep: Int) {
            this()
            this.sleepGuard = sleep
            this.sleepInstantiate = sleep
            this.sleepApply = sleep
        }

        def this(sleepGuard: Int, sleepInstantiate: Int, sleepApply: Int, memoization: Boolean) = {
            this()
            this.sleepGuard = sleepGuard
            this.sleepInstantiate = sleepInstantiate
            this.sleepApply = sleepApply
            this.memoization = memoization
        }

        def this(sleep: Int, memoization: Boolean) {
            this()
            this.sleepGuard = sleep
            this.sleepInstantiate = sleep
            this.sleepApply = sleep
            this.memoization = memoization
        }

        def _sleepGuard: Int = this.sleepGuard
        def _sleepInstantiate: Int = this.sleepInstantiate
        def _sleepApply: Int = this.sleepApply
        def _memoization: Boolean = this.memoization

        def set_sleepGuard(sleep: Int): Unit = this.sleepGuard = sleep
        def set_sleepInstantiate(sleep: Int): Unit = this.sleepInstantiate = sleep
        def set_sleepApply(sleep: Int): Unit = this.sleepApply = sleep
        def set_memoization(memoization: Boolean): Unit = this.memoization = memoization

    }

    // Storage level option
    final val STORAGE_MEMORY_ONLY = "MEMORY_ONLY"
    final val STORAGE_DISK_ONLY = "DISK_ONLY"
    final val STORAGE_MEMORY_ONLY_SER = "MEMORY_ONLY_SER"
    final val STORAGE_MEMORY_AND_DISK = "MEMORY_AND_DISK"
    final val STORAGE_MEMORY_AND_DISK_SER = "MEMORY_AND_DISK_SER"

    //        Storage Level    Space used  CPU time  In memory  On-disk  Serialized   Recompute some partitions
    //        ----------------------------------------------------------------------------------------------------
    //        MEMORY_ONLY          High        Low       Y          N        N         Y
    //        MEMORY_ONLY_SER      Low         High      Y          N        Y         Y
    //        MEMORY_AND_DISK      High        Medium    Some       Some     Some      N
    //        MEMORY_AND_DISK_SER  Low         High      Some       Some     Y         N
    //        DISK_ONLY            Low         High      N          Y        Y         N


    class ConfigSpark {

        var nexecutors: Int = 1
        var npartitions: Int = 1
        var sparkcontext: SparkContext = null
        var executorMemory: String = "1g"
        var storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK

        def this(sc: SparkContext, nexecutors: Int, npartitions: Int, executorMemory: String, storage: StorageLevel) = {
            this()
            this.nexecutors = nexecutors
            this.executorMemory = executorMemory
            this.npartitions = npartitions
            this.sparkcontext = sc
            this.storageLevel = storage
        }

        def _nexecutors: Int = this.nexecutors
        def _executorMemory: String = this.executorMemory
        def _ncores: Int = this._sparkcontext.getConf.get("spark.executor.cores").toInt
        def _npartitions: Int = this.npartitions
        def _sparkcontext: SparkContext = this.sparkcontext
        def _storageLevel: StorageLevel = this.storageLevel

        def set_nexecutors(nexecutors: Int): Unit = this.nexecutors = nexecutors
        def set_executorMemory(executorMemory: String): Unit = this.executorMemory = executorMemory
        def set_npartitions(npartitions: Int): Unit = this.npartitions = npartitions
        def set_sparkcontext(sc: SparkContext): Unit = this.sparkcontext = sc
        def set_storageLevel(storage: StorageLevel): Unit = this.storageLevel = storage

    }

    class Config {

        var configEngine: ConfigEngine = new ConfigEngine()
        var configModel: ConfigModel = new ConfigModel()
        var configTransformation: ConfigTransformation = new ConfigTransformation()
        var configSpark: ConfigSpark = new ConfigSpark()

        def this(ce: ConfigEngine, cm: ConfigModel, ct: ConfigTransformation, cs: ConfigSpark) = {
            this()
            this.set_configEngine(ce)
            this.set_configModel(cm)
            this.set_configTransformation(ct)
            this.set_configSpark(cs)
        }

        def set_configEngine(ce: Parameters.ConfigEngine): Unit = {
            this.configEngine.set_tuples(ce._tuples)
            this.configEngine.set_tls(ce._tls)
            this.configEngine.set_tracerule(ce._tracerule)
            this.configEngine.set_collect(ce._collect)
            this.configEngine.set_bcast(ce._bcast)
            this.configEngine.set_distinctApply(ce._distinctApply)
        }

        def set_configModel(cm: ConfigModel): Unit = {
            this.configModel.set_linkType(cm._link_type)
        }

        def set_configTransformation(ct: ConfigTransformation): Unit = {
            this.configTransformation.set_sleepApply(ct._sleepApply)
            this.configTransformation.set_sleepInstantiate(ct._sleepInstantiate)
            this.configTransformation.set_sleepGuard(ct._sleepGuard)
        }

        def set_configSpark(cs: ConfigSpark): Unit = {
            this.configSpark.set_nexecutors(cs._nexecutors)
            this.configSpark.set_npartitions(cs._npartitions)
            this.configSpark.set_storageLevel(cs._storageLevel)
            this.configSpark.set_executorMemory(cs._executorMemory)
            this.configSpark.set_sparkcontext(cs._sparkcontext)
        }

        def _tuples: String = this.configEngine._tuples
        def _tls: String = this.configEngine._tls
        def _tracerule: Boolean = this.configEngine._tracerule
        def _collect: String = this.configEngine._collect
        def _bcast: String = this.configEngine._bcast
        def _withId: Boolean = this.configEngine._withId
        def _parallelExtract: Boolean = this.configEngine._parallelExtract
        def _distinctApply: String = this.configEngine._distinctApply
        def _link_type: String = this.configModel._link_type
        def _sleepGuard: Int = this.configTransformation._sleepGuard
        def _sleepInstantiate: Int = this.configTransformation._sleepInstantiate
        def _sleepApply: Int = this.configTransformation._sleepApply
        def _memoization: Boolean = this.configTransformation._memoization
        def _nexecutors: Int = this.configSpark._nexecutors
        def _ncores: Int = this.configSpark._nexecutors
        def _npartitions: Int = this.configSpark._npartitions
        def _sparkcontext: SparkContext = this.configSpark._sparkcontext
        def _storageLevel: StorageLevel = this.configSpark._storageLevel
        def _storageLevel_string: String = this.configSpark._storageLevel match {
            case StorageLevel.MEMORY_ONLY => STORAGE_MEMORY_ONLY
            case StorageLevel.DISK_ONLY => STORAGE_DISK_ONLY
            case StorageLevel.MEMORY_ONLY => STORAGE_MEMORY_ONLY_SER
            case StorageLevel.MEMORY_AND_DISK => STORAGE_MEMORY_AND_DISK
            case StorageLevel.MEMORY_AND_DISK_SER => STORAGE_MEMORY_AND_DISK_SER
        }
        def _executorMemory: String = this.configSpark._executorMemory

        def set_nexecutors(nexecutors: Int): Unit = this.configSpark.set_nexecutors(nexecutors)
        def set_npartitions(npartitions: Int): Unit = this.configSpark.set_npartitions(npartitions)
        def set_sparkcontext(sc: SparkContext): Unit = this.configSpark.set_sparkcontext(sc)
        def set_storageLevel(storage: StorageLevel): Unit = this.configSpark.set_storageLevel(storage)
        def set_executorMemory(mem: String): Unit = this.configSpark.set_executorMemory(mem)
        def set_sleepGuard(sleep: Int): Unit = this.configTransformation.set_sleepGuard(sleep)
        def set_sleepInstantiate(sleep: Int): Unit = this.configTransformation.set_sleepInstantiate(sleep)
        def set_sleepApply(sleep: Int): Unit = this.configTransformation.set_sleepApply(sleep)
        def set_linkType (linkType: String): Unit = this.configModel.set_linkType(linkType)
        def set_tuples(tuples: String): Unit = this.configEngine.set_tuples(tuples)
        def set_tls(tls: String): Unit = this.configEngine.set_tls(tls)
        def set_tracerule(tracerule: Boolean): Unit = this.configEngine.set_tracerule(tracerule)
        def set_collect(collect: String): Unit = this.configEngine.set_collect(collect)
        def set_bcast(bcast: String): Unit = this.configEngine.set_bcast(bcast)
        def set_distinctApply(distinctApply: String): Unit = this.configEngine.set_distinctApply(distinctApply)
        def set_parallelExtract(parallelExtract: Boolean): Unit = this.configEngine.set_parallelExtract(parallelExtract)
        def set_memoization(memoization: Boolean): Unit = this.configTransformation.set_memoization(memoization)

    }

}

