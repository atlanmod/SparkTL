package org.atlanmod.util

object StringUtil {
    def make_string_ln(value : List[String]) : String = value.mkString("\n") + "\n"
    def print_strings_ln(value : List[String]) : Unit = for(str <- value) println(str)
}
