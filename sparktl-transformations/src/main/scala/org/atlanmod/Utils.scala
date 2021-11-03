package org.atlanmod

object Utils {

    def my_sleep(millis: Int, rd: Int): Int = {
        var d = rd.toDouble
        val end = System.nanoTime() + millis * 1e6
        var current = System.nanoTime
        while(current < end){
            current = System.nanoTime
            d += 1.0
        }
        d.toInt
    }

}
