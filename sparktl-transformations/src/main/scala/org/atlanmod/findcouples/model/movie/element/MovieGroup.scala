package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.MovieElement

abstract class MovieGroup(classname : String) extends MovieElement (classname: String) {
    def getId: String
    def getAvgRating: Double
}
