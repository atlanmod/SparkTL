package org.atlanmod

object Profiler {

    def time[R](block: => R, printer: Boolean = true): R = {
        val t0 = System.nanoTime()
        val result = block    // call-by-name
        val t1 = System.nanoTime()
        if (printer) println("Elapsed time: " + (t1 - t0) / 1e6d + "ms")
        result
    }

}
