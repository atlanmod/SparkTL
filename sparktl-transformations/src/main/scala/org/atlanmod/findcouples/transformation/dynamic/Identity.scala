package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.Utils.my_sleep
import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link._
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodel
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieModel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

import scala.util.Random

object Identity {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_CLIQUE = "clique"
    final val PATTERN_COUPLE = "couple"

    val random: Random.type = scala.util.Random

    def makePersonMovies(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, metamodel: MovieMetamodel,
                         input_person: MoviePerson, output_person: MoviePerson): Option[MovieLink] =
        metamodel.getMoviesOfPerson(model, input_person) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_MOVIE, metamodel.MOVIE, ListUtils.listToListList(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new PersonToMovies(output_person, output_movies))
                    case _ => None
                }
            case None => None
        }

    def makeMoviePersons(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, metamodel: MovieMetamodel,
                         input_movie: MovieMovie, output_movie: MovieMovie): Option[MovieLink] =
        metamodel.getPersonsOfMovie(model, input_movie) match {
            case Some(persons) =>
                var actors: Option[List[MoviePerson]] = None
                var actress: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_ACTOR, metamodel.ACTOR, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // Then we get output actresses
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_ACTRESS, metamodel.ACTRESS, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActress]) => actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new MovieToPersons(output_movie, people))
                }
            case _ => None
        }

    def makeCoupleToPerson(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, metamodel: MovieMetamodel,
                           person: MoviePerson,
                           couple: MovieCouple, pattern: String, i: Int): Option[DynamicLink] = {
        val type_ : String = {
            pattern match {
                case PATTERN_ACTOR => metamodel.ACTOR
                case PATTERN_ACTRESS => metamodel.ACTRESS
                case _ => ""
            }
        }
        (Resolve.resolve(tls, model, metamodel.metamodel, pattern, type_, List(person)), i) match {
            case (Some(act: MoviePerson), 1) => Some(new CoupleToPersonP1(couple, act))
            case (Some(act: MoviePerson), 2) => Some(new CoupleToPersonP2(couple, act))
            case _ => None
        }
    }

    def makeGroupMovies(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, metamodel: MovieMetamodel,
                        input_group: MovieGroup, output_group: MovieGroup): Option[MovieLink] = {
        metamodel.getMoviesOfGroup(model, input_group) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_MOVIE, metamodel.MOVIE, ListUtils.singleton(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new GroupToMovies(output_group, output_movies))
                    case _ => None
                }
            case _ => None
        }
    }

    def makeCliquePersons(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,  metamodel: MovieMetamodel,
                          input_clique: MovieClique, output_clique: MovieClique): Option[MovieLink] = {
        metamodel.getPersonsOfClique(model, input_clique) match {
            case Some(persons) =>
                var actors: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_ACTOR, metamodel.PERSON, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                    case _ => None
                }
                // Then we get output actresses
                var actress: Option[List[MoviePerson]] = None
                Resolve.resolveAll(tls, model, metamodel.metamodel, PATTERN_ACTRESS, metamodel.PERSON, ListUtils.singletons(persons)) match {
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

    def makeCoupleToPersonP1(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,  metamodel: MovieMetamodel,
                             input_couple: MovieCouple, output_couple: MovieCouple): Option[MovieLink] = {
        metamodel.getPersonP1OfCouple(model, input_couple) match {
            case Some(person) =>
                var output_person = Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_ACTOR, metamodel.PERSON, List(person))
                if (output_person.isEmpty)
                    output_person = Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_ACTRESS, metamodel.PERSON, List(person))
                output_person match {
                    case Some(person: MoviePerson) => Some(new CoupleToPersonP1(output_couple, person))
                    case _ => None
                }
        }
    }

    def makeCoupleToPersonP2(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, metamodel: MovieMetamodel,
                             input_couple: MovieCouple, output_couple: MovieCouple): Option[MovieLink] = {
        metamodel.getPersonP2OfCouple(model, input_couple) match {
            case Some(person) =>
                var output_person = Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_ACTOR, metamodel.PERSON, List(person))
                if (output_person.isEmpty)
                    output_person = Resolve.resolve(tls, model, metamodel.metamodel, PATTERN_ACTRESS, metamodel.PERSON, List(person))
                output_person match {
                    case Some(person: MoviePerson) => Some(new CoupleToPersonP2(output_couple, person))
                    case _ => None
                }
        }
    }

    def identity_imdb(metamodel: MovieMetamodel, sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "movie2movie",
                    types = List(metamodel.MOVIE),
                    from = (_, _) => {
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
                                        makeMoviePersons(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieMovie], output.asInstanceOf[MovieMovie])
                                    }
                                ))
                        )
                    )
                ),
                new RuleImpl(
                    name = "actor2actor",
                    types = List(metamodel.ACTOR),
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
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieActor], output.asInstanceOf[MovieActor])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "actress2actress",
                    types = List(metamodel.ACTRESS),
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
                                        makePersonMovies(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieActress], output.asInstanceOf[MovieActress])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "clique",
                    types = List(metamodel.CLIQUE),
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
                                        makeGroupMovies(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieClique], output.asInstanceOf[MovieClique])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCliquePersons(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieClique], output.asInstanceOf[MovieClique])
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple",
                    types = List(metamodel.COUPLE),
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
                                        makeGroupMovies(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieCouple], output.asInstanceOf[MovieCouple])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCoupleToPersonP1(tls, sm.asInstanceOf[MovieModel], metamodel,
                                            pattern.head.asInstanceOf[MovieCouple], output.asInstanceOf[MovieCouple])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeCoupleToPersonP2(tls, sm.asInstanceOf[MovieModel], metamodel,
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
