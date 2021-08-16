package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.MovieElement

abstract class MoviePerson (classname : String) extends MovieElement (classname: String) {
    def getName: String
}
