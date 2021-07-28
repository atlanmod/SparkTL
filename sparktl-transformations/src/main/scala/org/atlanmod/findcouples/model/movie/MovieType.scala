package org.atlanmod.findcouples.model.movie

object MovieType extends Enumeration {
    protected case class MovieTypeVal(name: String) extends super.Val{}

    import scala.language.implicitConversions
    implicit def valueToMovieTypeVal(x: Value): MovieTypeVal = x.asInstanceOf[MovieTypeVal]

    //type MovieType = Value
    val MOVIE: MovieTypeVal = MovieTypeVal("movie")
    val VIDEO: MovieTypeVal = MovieTypeVal("video")
    val TV: MovieTypeVal = MovieTypeVal("tv")
    val VIDEOGAME: MovieTypeVal = MovieTypeVal("video-game")

}
