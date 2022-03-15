package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.IdGenerator
import org.atlanmod.findcouples.model.movie.MovieElement
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodelNaive

class MovieMovie extends MovieElement (MovieMetamodelNaive.MOVIE) {

    def this(id: Long, title: String, rating : Double, year: Int, movieType: MovieType.Value) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", movieType)
    }

    def this(id: Long, title: String, rating: Double, year: Int, movieType: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", MovieType.stringToMovieTypeVal(movieType)) // get movietype
    }


    def this(title: String, rating : Double, year: Int, movieType: MovieType.Value) = {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", movieType)
    }

    def this(title: String, rating: Double, year: Int, movieType: String) = {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", MovieType.stringToMovieTypeVal(movieType)) // get movietype
    }

    override def getId: Long = eGetProperty("id").asInstanceOf[Long]
    def getTitle: String = super.eGetProperty("title").asInstanceOf[String]
    def getRating: Double = super.eGetProperty("rating").asInstanceOf[Double]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int]
    def getMovieType: MovieType.Value = super.eGetProperty("type").asInstanceOf[MovieType.Value]

    override def toString: String = "[" + getMovieType.name + "] " + getYear + " " + getTitle + "(" + getRating + ")"

    override def equals(o: Any): Boolean = {
        o match {
            case obj: MovieMovie =>
                this.getTitle.equals(obj.getTitle) & this.getRating.equals(obj.getRating) &
                  this.getYear.equals(obj.getYear) & this.getMovieType.equals(obj.getMovieType)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}
