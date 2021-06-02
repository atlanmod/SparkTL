package org.atlanmod.transformation.dynamic

object Utils {

    def factorial(i: Int): Int = i match {
        case 0 => 1
        case n => n * factorial(n-1)
    }
    def dumb(n: Int, i: Int) : Unit =
        for (_ <- 1 to n) factorial(i)

    def very_dumb(): Unit =
        dumb(2000, 2000)

    def my_sleep(sec: Int): Unit = {
        val nb_loop = sec * 100
        for(k <- 1 to nb_loop)
            Thread.sleep(10)
//
//
//
//        val end = System.nanoTime() + sec * 1e9
//        var current = System.nanoTime
//        while(current < end){
//            current = System.nanoTime
//        }
    }

}
