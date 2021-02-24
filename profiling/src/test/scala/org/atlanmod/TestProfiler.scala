package org.atlanmod

import java.lang.Thread.sleep

import org.scalatest.funsuite.AnyFunSuite

class TestProfiler extends AnyFunSuite {

//
//    val t0 = System.nanoTime()
//    val result = block    // call-by-name
//    val t1 = System.nanoTime()
//    if (printer) println("Elapsed time: " + (t1 - t0) + "ns")

    def prg() = {
        val list = (1 to 100000).toList
        list.map(v => v % 4).sum
    }

    test("test unit") {
        Profiler.time{ sleep(10000) }
    }

    test("extra cost of profiling") {
        prg() // warm up
        val t0 = System.nanoTime()
        val ntest = 10
        for (_ <- 1 to ntest)
            Profiler.time(prg(), false)
        val t1 = System.nanoTime()
        val time_with_profiling = (t1 - t0) / ntest
        val t2 = System.nanoTime()
        for (_ <- 1 to ntest) prg()
        val t3 = System.nanoTime()
        val time_without_profiling = (t3 - t2) / ntest
        val overhead_ms = (time_with_profiling - time_without_profiling) / 1e6d
//        println(overhead_ms + "ms")
        assert(true)
    }


}
