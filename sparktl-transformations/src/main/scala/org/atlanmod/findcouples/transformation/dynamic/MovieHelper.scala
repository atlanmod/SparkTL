package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.findcouples.model.movie.element.{MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.MovieModel
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodel
import org.atlanmod.findcouples.transformation.dynamic.MovieHelper.memoize_candidate.getOrElseUpdate

import scala.collection.mutable

object MovieHelper {

    def helper_coactor(model: MovieModel, meta: MovieMetamodel, p: MoviePerson): List[MoviePerson] =
        meta.getMoviesOfPerson(model, p) match {
            case Some(movies: List[MovieMovie]) =>
                movies.flatMap(movie => meta.getPersonsOfMovieAsList(model, movie))
            case _ => List()
        }

    def helper_areCouple(model: MovieModel, meta: MovieMetamodel, p1: MoviePerson, p2: MoviePerson): Boolean =
        helper_commonMovies(model, meta, p1, p2).size >= 3 & !p1.equals(p2) & p1.getName < p2.getName

    def helper_commonMovies(model: MovieModel, meta: MovieMetamodel, p1: MoviePerson, p2: MoviePerson): List[MovieMovie] =
        (meta.getMoviesOfPerson(model, p1), meta.getMoviesOfPerson(model, p2)) match {
            case (Some(movies1), Some(movies2)) => movies1.intersect(movies2)
            case _ => List()
        }

    def helper_candidate(model: MovieModel, meta: MovieMetamodel, p: MoviePerson) : List[MoviePerson] = {
        val mvs = meta.getMoviesOfPerson(model, p).getOrElse(List())
        mvs.flatMap(mv => meta.getPersonsOfMovie(model, mv).getOrElse(List())).distinct.filter(per => per != p)
    }

    val memoize_candidate: mutable.HashMap[MoviePerson, List[MoviePerson]] = new mutable.HashMap()
    def helper_candidate_memo(model: MovieModel, meta: MovieMetamodel, p: MoviePerson) : List[MoviePerson] = {
        memoize_candidate.synchronized(getOrElseUpdate(p,
            meta.getMoviesOfPerson(model, p).getOrElse(List())
              .flatMap(mv => meta.getPersonsOfMovie(model, mv).getOrElse(List()))
              .distinct.filter(per => per != p)))
    }

}
