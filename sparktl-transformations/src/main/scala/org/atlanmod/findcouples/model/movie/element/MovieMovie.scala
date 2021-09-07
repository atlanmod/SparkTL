package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.{MovieElement, MovieMetamodel}

class MovieMovie extends MovieElement (MovieMetamodel.MOVIE) {

    def this(title: String, rating: Double, year: Int, movieType: MovieType.Value) = {
        this()
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", movieType)
    }

    def this(title: String, rating: Double, year: Int, movieType: String) = {
        this()
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", MovieType.stringToMovieTypeVal(movieType)) // get movietype
    }

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
