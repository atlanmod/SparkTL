package org.atlanmod.tl.util

import scala.util.Random

object IdUtils {

    var ids: List[String] = List()

    def listIds(): List[String] = {
        ids
    }

    def newId(length : Int = 10): String = {
        var s = ""
        do{
            s = Random.nextString(length)
        } while (ids.contains(s))
        ids = s :: ids
        s
    }

    def main(args: Array[String]): Unit = {
        for(_ <- 1 to 10) newId()
        println(listIds())
    }
}
