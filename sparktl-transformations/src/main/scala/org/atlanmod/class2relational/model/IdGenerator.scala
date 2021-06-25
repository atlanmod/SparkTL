package org.atlanmod.class2relational.model

object IdGenerator {

    private val DEFAULT_LENGTH: Int = 4
    private var ids: List[String] = List()
    private var last = 0

    private def max_val(size: Int) = Math.pow(10.0, size).toInt

    def id(): String = {
        val res = last
        last = last + 1
        res.toString
    }

    //    def id(): String = {
    //        id(DEFAULT_LENGTH)
    //    }

    def id(length: Int): String = {
        val r = scala.util.Random
        var id: String = r.nextInt(max_val(length)).toString
        do {
            id = r.nextInt(max_val(length)).toString
        } while (ids.contains(id))
        ids = id :: ids
        id
    }

}
