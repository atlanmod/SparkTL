import org.apache.spark.{SparkConf, SparkContext}

object Main {

    def main(args: Array[String]) : Unit = {
        val conf = new SparkConf()
        conf.setAppName("Lab")
        conf.setMaster("local[4]")
        val sc = new SparkContext(conf)
        val NUM_SAMPLES = 500000000
        val startTimeMillis = System.currentTimeMillis()
        var count = sc.parallelize(1 to NUM_SAMPLES).filter { _ =>
            val x = math.random
            val y = math.random
            x*x + y*y < 1
        }.count() * 4/(NUM_SAMPLES.toFloat)
        val endTimeMillis = System.currentTimeMillis()
        val durationSeconds = endTimeMillis - startTimeMillis
        println(durationSeconds + " ms")
    }

}


