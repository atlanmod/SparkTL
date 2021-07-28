package org.atlanmod.findcouples.model.movie

class MovieMovie extends MovieElement (MovieMetamodel.MOVIE) {

    def this(title: String, rating: Double, year: Int, movieType: MovieType.Value) = {
        this()
        super.eSetProperty("title", title)
        super.eSetProperty("rating", rating)
        super.eSetProperty("year", year)
        super.eSetProperty("type", movieType)
    }

    def getTitle: String = super.eGetProperty("title").asInstanceOf[String]
    def getRating: Double = super.eGetProperty("rating").asInstanceOf[Double]
    def getYear: Int = super.eGetProperty("year").asInstanceOf[Int]
    def getMovieType: MovieType.Value = super.eGetProperty("type").asInstanceOf[MovieType.Value]

    override def toString: String = "[" + getMovieType.name + "] " + getYear + " " + getTitle + "(" + getRating + ")"

}
