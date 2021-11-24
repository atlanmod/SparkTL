package org.atlanmod.findcouples

import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link.MovieToPersons
import org.atlanmod.findcouples.model.movie.MovieModel
import org.atlanmod.findcouples.model.movie.element.{MovieActor, MovieActress, MovieMovie, MoviePerson, MovieType}
import org.atlanmod.findcouples.model.movie.link.{MovieToPersons, PersonToMovies}
import org.atlanmod.findcouples.model.movie.{MovieElement, MovieLink, MovieModel}

object ModelSamples {
    val rmd = new scala.util.Random

    def flipcoin(): Int = rdmInt(0, 1)

    def rdmDouble(min:Int = 0, max:Int = 1000): Double = {
        rdmInt(min,max-1) +
        (flipcoin() match {
            case 0 => 0.5
            case 1 => 1.0
        })
    }

    def rdmInt(min:Int = 0, max:Int = 1000): Int = {
        min + rmd.nextInt( (max - min) + 1 )
    }

    def getReplicatedSimple(size: Int): MovieModel = {
        var elements: List[MovieElement] = List()
        var links: List[MovieLink] = List()

        val mod: Double = size % 13
        val loop: Int = scala.math.max(1, if (mod >= 7) (size / 13) + 1 else size / 13)

        for (i <- 1 to loop) {
            // 13 elements
            val act1 = new MovieActor("actor1_"+i)
            val act2 = new MovieActress("actress2_"+i)
            val act3 = new MovieActor("actor3_"+i)
            val act4 = new MovieActor("actor4_"+i)
            val act5 = new MovieActor("actor5_"+i)
            val act6 = new MovieActor("actor6_"+i)
            val act7 = new MovieActress("actress7_"+i)
            val mov1 = new MovieMovie("Episode 1_"+i, 6.5, rdmInt(1950, 2020), MovieType.MOVIE)
            val mov2 = new MovieMovie("Episode 2_"+i, 6.0, rdmInt(1950, 2020), MovieType.MOVIE)
            val mov3 = new MovieMovie("Episode 3_"+i, 9.0, rdmInt(1950, 2020), MovieType.MOVIE)
            val mov4 = new MovieMovie("Episode 4_"+i, 8.0, rdmInt(1950, 2020), MovieType.MOVIE)
            val mov5 = new MovieMovie("Episode 5_"+i, 9.0, rdmInt(1950, 2020), MovieType.MOVIE)
            val mov6 = new MovieMovie("Episode 6_"+i, 9.5, rdmInt(1950, 2020), MovieType.MOVIE)
            val actors: List[MoviePerson] = List(act1, act2, act3, act4, act5, act6, act7)
            val movies: List[MovieMovie] = List(mov5, mov1, mov2, mov3, mov4, mov6)
            elements = actors ++ movies ++ elements

            val mov1_actors = new MovieToPersons(mov1, List(act2, act3))
            val mov2_actors = new MovieToPersons(mov2, List(act2, act3, act4))
            val mov3_actors = new MovieToPersons(mov3, List(act2, act3, act4))
            val mov4_actors = new MovieToPersons(mov4, List(act5, act7, act6))
            val mov5_actors = new MovieToPersons(mov5, List(act5, act7, act6, act1))
            val mov6_actors = new MovieToPersons(mov6, List(act5, act7, act6, act1))
            val act1_movies = new PersonToMovies(act1, List(mov5, mov6))
            val act2_movies = new PersonToMovies(act2, List(mov1, mov2, mov3))
            val act3_movies = new PersonToMovies(act3, List(mov1, mov2, mov3))
            val act4_movies = new PersonToMovies(act4, List(mov2, mov3))
            val act5_movies = new PersonToMovies(act5, List(mov4, mov5, mov6))
            val act6_movies = new PersonToMovies(act6, List(mov4, mov5, mov6))
            val act7_movies = new PersonToMovies(act7, List(mov4, mov5, mov6))
            val m2p: List[MovieToPersons] = List(mov1_actors, mov2_actors, mov3_actors, mov4_actors, mov5_actors, mov6_actors)
            val p2m: List[PersonToMovies] = List(act1_movies, act2_movies, act3_movies, act4_movies, act5_movies, act6_movies, act7_movies)
            links = m2p ++ p2m ++ links
        }
        new MovieModel(elements, links)
    }

}
