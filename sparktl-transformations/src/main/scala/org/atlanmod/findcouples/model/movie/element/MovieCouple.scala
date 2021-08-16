package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.MovieMetamodel

class MovieCouple extends MovieGroup (MovieMetamodel.COUPLE) {

    def this(avgRating: Double) = {
        this()
        super.eSetProperty("avgRating", avgRating)
    }

    override def getAvgRating: Double = super.eGetProperty("avgRating").asInstanceOf[Double]

    override def toString: String = "Couple (" + getAvgRating + ")"

}
