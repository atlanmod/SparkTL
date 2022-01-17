package org.atlanmod.findcouples.model.movie.metamodel

import org.atlanmod.findcouples.model.movie.MovieModel
import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait MovieMetamodel {

    final val MOVIE = "Movie"
    final val GROUP = "Group"
    final val COUPLE = "Couple"
    final val CLIQUE = "Clique"
    final val PERSON = "Person"
    final val ACTOR = "Actor"
    final val ACTRESS = "Actress"

    final val CLIQUE_PERSONS: String = "persons"
    final val COUPLE_PERSON_P1: String = "p1"
    final val COUPLE_PERSON_P2: String = "p2"
    final val GROUP_MOVIES: String = "commonMovies"
    final val PERSON_MOVIES: String = "movies"
    final val MOVIE_PERSONS: String = "persons"

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("MovieMetamodel")

    def getPersonsOfMovieAsList(model: MovieModel, movie: MovieMovie): List[MoviePerson]
    def getPersonsOfMovie(model: MovieModel, movie: MovieMovie): Option[List[MoviePerson]]
    def getMoviesOfPerson(model: MovieModel, person: MoviePerson): Option[List[MovieMovie]]
    def getMoviesOfGroup(model: MovieModel, group: MovieGroup): Option[List[MovieMovie]]
    def getPersonsOfClique(model: MovieModel, clique: MovieClique): Option[List[MoviePerson]]
    def getPersonP1OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson]
    def getPersonP2OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson]
    def getAllCouple(model: MovieModel): List[MovieCouple]
    def getAllCoupleTriplets(model: MovieModel): List[(MovieCouple, MoviePerson, MoviePerson)]
    def getAllActors(model: MovieModel): List[MovieActor]
    def getAllMovies(model: MovieModel): List[MovieMovie]
}
