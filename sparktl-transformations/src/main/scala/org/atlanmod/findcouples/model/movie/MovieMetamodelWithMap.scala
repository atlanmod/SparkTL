package org.atlanmod.findcouples.model.movie

import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object MovieMetamodelWithMap {

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

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("MovieMetamodel")

    def getPersonsOfMovieAsList(model: MovieModel, movie: MovieMovie): List[MoviePerson] =
        metamodel.allLinksOfTypeOfElement(movie, MOVIE_PERSONS, model) match {
            case Some(e : List[MoviePerson]) => e
            case _ => List()
        }

    def getPersonsOfMovie(model: MovieModel, movie: MovieMovie): Option[List[MoviePerson]] =
        metamodel.allLinksOfTypeOfElement(movie, MOVIE_PERSONS, model) match {
            case Some(e : List[MoviePerson]) => Some(e)
            case _ => None
        }

    def getMoviesOfPerson(model: MovieModel, person: MoviePerson): Option[List[MovieMovie]] =
        metamodel.allLinksOfTypeOfElement(person, PERSON_MOVIES, model) match {
            case Some(e : List[MovieMovie]) => Some(e)
            case _ => None
        }

    def getMoviesOfGroup(model: MovieModel, group: MovieGroup): Option[List[MovieMovie]] =
        metamodel.allLinksOfTypeOfElement(group, GROUP_MOVIES, model) match {
            case Some(e : List[MovieMovie]) => Some(e)
            case _ => None
        }

    def getPersonsOfClique(model: MovieModel, clique: MovieClique): Option[List[MoviePerson]] =
        metamodel.allLinksOfTypeOfElement(clique, CLIQUE_PERSONS, model) match {
            case Some(e : List[MoviePerson]) => Some(e)
            case _ => None
        }


    def getPersonP1OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson] =
        metamodel.allLinksOfTypeOfElement(couple, COUPLE_PERSON_P1, model) match {
            case Some(e: List[MoviePerson]) => e.headOption
            case _ => None
        }

    def getPersonP2OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson] =
        metamodel.allLinksOfTypeOfElement(couple, COUPLE_PERSON_P1, model) match {
            case Some(e: List[MoviePerson]) => e.headOption
            case _ => None
        }

    def getAllCoupleTriplets(model: MovieModel): List[(MovieCouple, MoviePerson, MoviePerson)] =
        getAllCouple(model).flatMap(couple =>
            (getPersonP1OfCouple(model, couple),getPersonP2OfCouple(model, couple)) match {
                case (Some(p1), Some(p2)) => List((couple, p1, p2))
                case _ => List()
            }
        )

    def getAllActors(model: MovieModel): List[MovieActor] =
        metamodel.allModelElementsOfType(ACTOR, model).asInstanceOf[List[MovieActor]]

    def getAllActresses(model: MovieModel): List[MovieActress] =
        metamodel.allModelElementsOfType(ACTRESS, model).asInstanceOf[List[MovieActress]]

    def getAllMovies(model: MovieModel): List[MovieMovie] =
        metamodel.allModelElementsOfType(MOVIE, model).asInstanceOf[List[MovieMovie]]

    def getAllCouple(model: MovieModel): List[MovieCouple] =
        metamodel.allModelElementsOfType(COUPLE, model).asInstanceOf[List[MovieCouple]]

    def getAllClique(model: MovieModel): List[MovieClique] =
        metamodel.allModelElementsOfType(CLIQUE, model).asInstanceOf[List[MovieClique]]

    def getAllGroup(model: MovieModel): List[MovieGroup] =
        getAllClique(model) ++ getAllCouple(model)

    def getAllPerson(model: MovieModel): List[MoviePerson] =
        getAllActors(model) ++ getAllActresses(model)

}
