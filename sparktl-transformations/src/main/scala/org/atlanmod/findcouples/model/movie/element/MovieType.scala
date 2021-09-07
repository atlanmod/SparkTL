package org.atlanmod.findcouples.model.movie.element

object MovieType extends Enumeration {

    protected case class MovieTypeVal(name: String) extends super.Val {}

    import scala.language.implicitConversions

    implicit def valueToMovieTypeVal(x: Value): MovieTypeVal = x.asInstanceOf[MovieTypeVal]

    implicit def stringToMovieTypeVal(x: String): MovieTypeVal =
        x match {
            case "movie" => MOVIE
            case "video" => VIDEO
            case "tv" => TV
            case "video-game" => VIDEOGAME
        }

    //type MovieType = Value
    val MOVIE: MovieTypeVal = MovieTypeVal("movie")
    val VIDEO: MovieTypeVal = MovieTypeVal("video")
    val TV: MovieTypeVal = MovieTypeVal("tv")
    val VIDEOGAME: MovieTypeVal = MovieTypeVal("video-game")

}
