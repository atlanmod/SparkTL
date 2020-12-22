package org.atlanmod.util

object RUtil {

    def r_vector(name: String, times: List[Any]) : String ={
        var res = name + " <- c("
        for (r <- 0 until times.length)
            res = res + times(r) + (if (r == times.size - 1) ")" else ",")
        res
    }

}
