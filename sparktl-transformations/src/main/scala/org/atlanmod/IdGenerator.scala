package org.atlanmod

object IdGenerator {

  private val DEFAULT_LENGTH: Int = 4
  private var ids: List[Long] = List()
  private var last: Long = 0

  private def max_val(size: Int) = Math.pow(10.0, size).toInt

  def id(): Long = {
    val res = last
    last = last + 1
    res
  }

  def id(length: Int): Long = {
    val r = scala.util.Random
    var id: Long = r.nextInt(max_val(length))
    do {
      id = r.nextInt(max_val(length))
    } while (ids.contains(id))
    ids = id :: ids
    id
  }

}
