package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.IdGenerator
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodelNaive

class MovieCouple extends MovieGroup (MovieMetamodelNaive.COUPLE) {

    def this(avgRating: Double) = {
        this()
        val id = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("avgRating", avgRating)
    }

    def this(id: Long, avgRating: Double) = {
        this()
        super.eSetProperty("avgRating", avgRating)
        super.eSetProperty("id", id)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    override def getAvgRating: Double = super.eGetProperty("avgRating").asInstanceOf[Double]

    override def toString: String = "Couple (" + getAvgRating + ")"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: MovieCouple =>
                this.getId.equals(obj.getId) & this.getAvgRating.equals(obj.getAvgRating)
            case _ => false
        }
}

    override def weak_equals(o: Any): Boolean = equals(o)


}
