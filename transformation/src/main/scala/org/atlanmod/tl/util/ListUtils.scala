package org.atlanmod.tl.tool

object ListUtils {

    def optionToList[A](o: Option[A]) : List[A] =
        o match {
            case Some(a) => List(a)
            case None => List()
        }

}
