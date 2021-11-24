package org.atlanmod.findcouples.model.movie

import org.atlanmod.findcouples.model.movie.element.MovieClique
import org.atlanmod.findcouples.model.movie.link.MovieToPersons
import org.atlanmod.findcouples.model.movie.element.{MovieActor, MovieClique, MovieCouple, MovieGroup, MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.link.{CliqueToPersons, CoupleToPersonP1, CoupleToPersonP2, GroupToMovies, MovieToPersons, PersonToMovies}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object MovieMetamodel {

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

    private def getActorsOfMovieOnLinks(links: Iterator[MovieLink], movie: MovieMovie): Option[List[MoviePerson]] =
        links.find(l => l.isInstanceOf[MovieToPersons] && l.getSource.equals(movie)) match {
            case Some(l: MovieToPersons) => Some(l.getTarget)
            case _ => None
        }

    def getPersonsOfMovieAsList(model: MovieModel, movie: MovieMovie): List[MoviePerson] =
        getPersonsOfMovie(model, movie) match {
            case Some(persons) => persons
            case _ => List()
        }

    def getPersonsOfMovie(model: MovieModel, movie: MovieMovie): Option[List[MoviePerson]] =
        getActorsOfMovieOnLinks(model.allModelLinks.toIterator, movie)

    private def getMoviesOfPersonOnLinks(links: Iterator[MovieLink], person: MoviePerson): Option[List[MovieMovie]] =
        links.find(l => l.isInstanceOf[PersonToMovies] && l.getSource.equals(person)) match {
            case Some(l: PersonToMovies) => Some(l.getTarget)
            case _ => None
        }

    def getMoviesOfPerson(model: MovieModel, person: MoviePerson): Option[List[MovieMovie]] =
        getMoviesOfPersonOnLinks(model.allModelLinks.toIterator, person)

    private def getMoviesOfGroupOnLinks(links: Iterator[MovieLink], group: MovieGroup): Option[List[MovieMovie]] =
        links.find(l => l.isInstanceOf[GroupToMovies] && l.getSource.equals(group)) match {
            case Some(l: GroupToMovies) => Some(l.getTarget)
            case _ => None
        }

    def getMoviesOfGroup(model: MovieModel, group: MovieGroup): Option[List[MovieMovie]] =
        getMoviesOfGroupOnLinks(model.allModelLinks.toIterator, group)

    private def getPersonsOfCliqueOnLinks(links: Iterator[MovieLink], clique: MovieClique): Option[List[MoviePerson]] =
        links.find(l => l.isInstanceOf[CliqueToPersons] && l.getSource.equals(clique)) match {
            case Some(l: CliqueToPersons) => Some(l.getTarget)
            case _ => None
        }

    def getPersonsOfClique(model: MovieModel, clique: MovieClique): Option[List[MoviePerson]] =
        getPersonsOfCliqueOnLinks(model.allModelLinks.toIterator, clique)

    def getPersonP1OfCoupleOnLinks(links: Iterator[MovieLink], couple: MovieCouple): Option[MoviePerson] =
        links.find(l => l.isInstanceOf[CoupleToPersonP1] && l.getSource.asInstanceOf[MovieCouple] == couple) match {
            case Some(l: CoupleToPersonP1) => Some(l.getPersonP1)
            case _ => None
        }

    def getPersonP1OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson] =
        getPersonP1OfCoupleOnLinks(model.allModelLinks.toIterator, couple)

    def getPersonP2OfCoupleOnLinks(links: Iterator[MovieLink], couple: MovieCouple): Option[MoviePerson] =
        links.find(l => l.isInstanceOf[CoupleToPersonP2] && l.getSource.asInstanceOf[MovieCouple] == couple) match {
            case Some(l: CoupleToPersonP2) => Some(l.getPersonP2)
            case _ => None
        }

    def getPersonP2OfCouple(model: MovieModel, couple: MovieCouple): Option[MoviePerson] =
        getPersonP2OfCoupleOnLinks(model.allModelLinks.toIterator, couple)

    def getAllCouple(model: MovieModel): List[MovieCouple] =
        model.allModelElements.filter(e => e.isInstanceOf[MovieCouple]).map(e => e.asInstanceOf[MovieCouple]).toList

    def getAllCoupleTriplets(model: MovieModel): List[(MovieCouple, MoviePerson, MoviePerson)] =
        getAllCouple(model).flatMap(couple =>
            (getPersonP1OfCouple(model, couple),getPersonP2OfCouple(model, couple)) match {
                case (Some(p1), Some(p2)) => List((couple, p1, p2))
                case _ => List()
            }
        )

    def getAllActors(model: MovieModel): List[MovieActor] =
        model.allModelElements.filter(m => m.isInstanceOf[MovieActor]).map(m => m.asInstanceOf[MovieActor]).toList

    def getAllMovies(model: MovieModel): List[MovieMovie] =
        model.allModelElements.filter(m => m.isInstanceOf[MovieMovie]).map(m => m.asInstanceOf[MovieMovie]).toList
}
