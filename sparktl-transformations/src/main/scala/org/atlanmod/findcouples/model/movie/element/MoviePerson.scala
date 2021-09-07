package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.MovieElement

abstract class MoviePerson (classname : String) extends MovieElement (classname: String) {
    def getName: String

    override def equals(o: Any): Boolean = {
        o match {
            case obj: MoviePerson =>
                this.getName.equals(obj.getName)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)
}
