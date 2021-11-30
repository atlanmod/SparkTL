package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.Utils.my_sleep
import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link._
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodelWithMap, MovieModel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

import scala.util.Random

object IdentityWithMap {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_CLIQUE = "clique"
    final val PATTERN_COUPLE = "couple"

    val mm: DynamicMetamodel[DynamicElement, DynamicLink] = MovieMetamodelWithMap.metamodel
    val random: Random.type = scala.util.Random

    def makePersonMovies(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                         input_person: MoviePerson, output_person: MoviePerson): Option[MovieLink] =
        MovieMetamodelWithMap.getMoviesOfPerson(model, input_person) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, mm, PATTERN_MOVIE, MovieMetamodelWithMap.MOVIE, ListUtils.listToListList(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new PersonToMovies(output_person, output_movies))
                    case _ => None
                }
            case None => None
        }

    def makeMoviePersons(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                         input_movie: MovieMovie, output_movie: MovieMovie): Option[MovieLink] =
        MovieMetamodelWithMap.getPersonsOfMovie(model, input_movie) match {
            case Some(persons) =>
                var actors: Option[List[MoviePerson]] = None
                var actress: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTOR, MovieMetamodelWithMap.ACTOR, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // Then we get output actresses
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTRESS, MovieMetamodelWithMap.ACTRESS, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActress]) => actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new MovieToPersons(output_movie, people))
                }
            case _ => None
        }

    def makeCoupleToPerson(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, person: MoviePerson,
                           couple: MovieCouple, pattern: String, i: Int): Option[DynamicLink] = {
        val type_ : String = {
            pattern match {
                case PATTERN_ACTOR => MovieMetamodelWithMap.ACTOR
                case PATTERN_ACTRESS => MovieMetamodelWithMap.ACTRESS
                case _ => ""
            }
        }
        (Resolve.resolve(tls, model, mm, pattern, type_, List(person)), i) match {
            case (Some(act: MoviePerson), 1) => Some(new CoupleToPersonP1(couple, act))
            case (Some(act: MoviePerson), 2) => Some(new CoupleToPersonP2(couple, act))
            case _ => None
        }
    }

    def makeGroupMovies(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                        input_group: MovieGroup, output_group: MovieGroup): Option[MovieLink] = {
        MovieMetamodelWithMap.getMoviesOfGroup(model, input_group) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, mm, PATTERN_MOVIE, MovieMetamodelWithMap.MOVIE, ListUtils.singleton(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new GroupToMovies(output_group, output_movies))
                    case _ => None
                }
            case _ => None
        }
    }

    def makeCliquePersons(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                          input_clique: MovieClique, output_clique: MovieClique): Option[MovieLink] = {
        MovieMetamodelWithMap.getPersonsOfClique(model, input_clique) match {
            case Some(persons) =>
                var actors: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTOR, MovieMetamodelWithMap.PERSON, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                    case _ => None
                }
                // Then we get output actresses
                var actress: Option[List[MoviePerson]] = None
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTRESS, MovieMetamodelWithMap.PERSON, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                    case _ => None
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new CliqueToPersons(output_clique, people))
                    case None => None
                }
            case _ => None
        }
    }

    def makeCoupleToPersonP1(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                             input_couple: MovieCouple, output_couple: MovieCouple): Option[MovieLink] = {
        MovieMetamodelWithMap.getPersonP1OfCouple(model, input_couple) match {
            case Some(person) =>
                var output_person = Resolve.resolve(tls, model, mm, PATTERN_ACTOR, MovieMetamodelWithMap.PERSON, List(person))
                if (output_person.isEmpty)
                    output_person = Resolve.resolve(tls, model, mm, PATTERN_ACTRESS, MovieMetamodelWithMap.PERSON, List(person))
                output_person match {
                    case Some(person: MoviePerson) => Some(new CoupleToPersonP1(output_couple, person))
                    case _ => None
                }
        }
    }

    def makeCoupleToPersonP2(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                             input_couple: MovieCouple, output_couple: MovieCouple): Option[MovieLink] = {
        MovieMetamodelWithMap.getPersonP2OfCouple(model, input_couple) match {
            case Some(person) =>
                var output_person = Resolve.resolve(tls, model, mm, PATTERN_ACTOR, MovieMetamodelWithMap.PERSON, List(person))
                if (output_person.isEmpty)
                    output_person = Resolve.resolve(tls, model, mm, PATTERN_ACTRESS, MovieMetamodelWithMap.PERSON, List(person))
                output_person match {
                    case Some(person: MoviePerson) => Some(new CoupleToPersonP2(output_couple, person))
                    case _ => None
                }
        }
    }

    def identity_imdb(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "movie2movie",
                    types = List(MovieMetamodelWithMap.MOVIE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MOVIE,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val movie = l.head.asInstanceOf[MovieMovie]
                                    Some(new MovieMovie(movie.getTitle, movie.getRating, movie.getYear, movie.getMovieType))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeMoviePersons(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieMovie], output.asInstanceOf[MovieMovie])
                                    }
                                ))
                        )
                    )
                ),
                new RuleImpl(
                    name = "actor2actor",
                    types = List(MovieMetamodelWithMap.ACTOR),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_ACTOR,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val actor = l.head.asInstanceOf[MovieActor]
                                    Some(new MovieActor(actor.getName))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActor], output.asInstanceOf[MovieActor])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "actress2actress",
                    types = List(MovieMetamodelWithMap.ACTRESS),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_ACTRESS,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val actress = l.head.asInstanceOf[MovieActress]
                                    Some(new MovieActress(actress.getName))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActress], output.asInstanceOf[MovieActress])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "clique",
                    types = List(MovieMetamodelWithMap.CLIQUE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_CLIQUE,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val clique = l.head.asInstanceOf[MovieClique]
                                    Some(new MovieClique(clique.getId, clique.getAvgRating))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeGroupMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieClique], output.asInstanceOf[MovieClique])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCliquePersons(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieClique], output.asInstanceOf[MovieClique])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple",
                    types = List(MovieMetamodelWithMap.COUPLE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val couple = l.head.asInstanceOf[MovieCouple]
                                    Some(new MovieCouple(couple.getId, couple.getAvgRating))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeGroupMovies(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieCouple], output.asInstanceOf[MovieCouple])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCoupleToPersonP1(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieCouple], output.asInstanceOf[MovieCouple])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCoupleToPersonP2(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieCouple], output.asInstanceOf[MovieCouple])
                                    }
                                )
                            )
                        )
                    )
                )
            )
        )

}
