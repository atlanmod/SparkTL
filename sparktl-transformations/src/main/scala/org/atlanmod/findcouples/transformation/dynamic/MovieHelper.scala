package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.findcouples.model.movie.element.{MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.{MovieMetamodel, MovieModel}

object MovieHelper {

    def helper_coactor(model: MovieModel, p: MoviePerson): List[MoviePerson] =
        MovieMetamodel.getMoviesOfPerson(model, p) match {
            case Some(movies: List[MovieMovie]) =>
                movies.flatMap(movie => MovieMetamodel.getPersonsOfMovieAsList(model, movie))
            case _ => List()
        }

    def helper_areCouple(model: MovieModel, p1: MoviePerson, p2: MoviePerson): Boolean =
        helper_commonMovies(model, p1, p2).size >= 3 & !p1.equals(p2) &
          (if (p1.getType.equals(p2.getType)) p1.getName < p2.getName else true)

    def helper_commonMovies(model: MovieModel, p1: MoviePerson, p2: MoviePerson): List[MovieMovie] =
        (MovieMetamodel.getMoviesOfPerson(model, p1), MovieMetamodel.getMoviesOfPerson(model, p2)) match {
            case (Some(movies1), Some(movies2)) => movies1.intersect(movies2)
            case _ => List()
        }


    def helper_candidate(model: MovieModel, p: MoviePerson) : List[MoviePerson] = {
        val mvs = MovieMetamodel.getMoviesOfPerson(model, p).getOrElse(List())
        mvs.flatMap(mv => MovieMetamodel.getPersonsOfMovie(model, mv).getOrElse(List()))
    }

}
