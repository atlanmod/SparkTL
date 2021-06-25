package org.atlanmod.tl.util

object ArithUtils {

    def indexes(length: Int): List[Int] = {
        length match {
            case 0 => List()
            case n => (n - 1) :: indexes(n - 1)
        }
    }

}
