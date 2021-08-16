package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.findcouples.model.movie._
import org.atlanmod.findcouples.model.movie.element.{MovieActor, MovieActress, MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.link.{MovieToPersons, PersonToMovies}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils


object FindCouples {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_COUPLE = "couple"

    val mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    /* TODO add a such block to actors and actress rules
        do {
            for (coauthor in thisModule.coactor(p1)){
                if (thisModule.areCouple(p1, coauthor) and p1.name.compareTo(coauthor.name)<0){
                    thisModule.createCouple(p1, coauthor);
                }
            }
        }
    */


    def helper_coactor(model: MovieModel, p: MoviePerson): List[MoviePerson] =
        MovieMetamodel.getMoviesOfPerson(model, p) match {
            case Some(movies: List[MovieMovie]) =>
                movies.flatMap(movie => MovieMetamodel.getPersonsOfMovieAsList(model, movie))
            case _ => List()
        }

    def helper_areCouple(model: MovieModel, p1: MoviePerson, p2: MoviePerson): Boolean =
        (MovieMetamodel.getMoviesOfPerson(model, p1), MovieMetamodel.getMoviesOfPerson(model, p2)) match {
            case (Some(movies1), Some(movies2)) => movies1.intersect(movies2).size > 3
            case _ => false
        }

    def makePersonMovies (tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                          input_person: MoviePerson, output_person: MoviePerson): Option[MovieLink] =
        MovieMetamodel.getMoviesOfPerson(model, input_person) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, mm, PATTERN_MOVIE, MovieMetamodel.MOVIE, ListUtils.singleton(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new PersonToMovies(output_person, output_movies))
                    case _ => None
                }
            case None => None
        }

    def makeMoviePersons (tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                          input_movie: MovieMovie, output_movie: MovieMovie): Option[MovieLink] =
        MovieMetamodel.getPersonsOfMovie(model, input_movie) match {
            case Some(persons) => {
                var actors: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTOR, MovieMetamodel.PERSON, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) =>  actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                    case _ => None
                }
                // Then we get output actresses
                var actress: Option[List[MoviePerson]] = None
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTRESS, MovieMetamodel.PERSON, ListUtils.singletons(persons) ) match {
                    case Some(l_act: List[MovieActor]) =>  actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                    case _ => None
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new MovieToPersons(output_movie, people))
                    case None => None
                }
            }
            case _ => None
        }

    def findcouples_imdb: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "movie",
                    types = List(MovieMetamodel.MOVIE),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MOVIE,
                            elementExpr = (_,_,l) =>
                                if (l.isEmpty) None else {
                                    val movie = l.head.asInstanceOf[MovieMovie]
                                    Some(new MovieMovie(movie.getTitle, movie.getRating, movie.getYear, movie.getMovieType))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeMoviePersons(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieMovie], output.asInstanceOf[MovieMovie])
                                ))
                        )
                    )
                ),
                new RuleImpl(
                    name = "actor",
                    types = List(MovieMetamodel.ACTOR),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_ACTOR,
                            elementExpr = (_,_,l) =>
                                if (l.isEmpty) None else {
                                    val actor = l.head.asInstanceOf[MovieActor]
                                    Some(new MovieActor(actor.getName))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActor], output.asInstanceOf[MovieActor])
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "actress",
                    types = List(MovieMetamodel.ACTRESS),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_ACTRESS,
                            elementExpr = (_,_,l) =>
                                if (l.isEmpty) None else {
                                    val actress = l.head.asInstanceOf[MovieActress]
                                    Some(new MovieActor(actress.getName))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActress], output.asInstanceOf[MovieActress])
                                )
                            )
                        )
                    )
                )
            )
        )

}
