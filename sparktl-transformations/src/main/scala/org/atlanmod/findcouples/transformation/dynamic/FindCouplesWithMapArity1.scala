package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.Utils.my_sleep
import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link.{CoupleToPersonP1, CoupleToPersonP2, MovieToPersons, PersonToMovies}
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieModel, MovieMetamodelWithMap => MovieMetamodel}
import org.atlanmod.findcouples.transformation.dynamic.MovieHelper.{helper_areCouple, helper_candidate, helper_commonMovies}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

import scala.util.Random

object FindCouplesWithMapArity1 {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_COUPLE_ACTOR_PERSON = "couple_actor_person"
    final val PATTERN_COUPLE_ACTRESS_PERSON = "couple_actress_person"

    val mm: DynamicMetamodel[DynamicElement, DynamicLink] = MovieMetamodel.metamodel
    val random: Random.type = scala.util.Random

    def makePersonMovies(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                         input_person: MoviePerson, output_person: MoviePerson): Option[MovieLink] =
        MovieMetamodel.getMoviesOfPerson(model, input_person) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, mm, PATTERN_MOVIE, MovieMetamodel.MOVIE, ListUtils.listToListList(movies)) match {
                    case Some(output_movies: List[MovieMovie]) => Some(new PersonToMovies(output_person, output_movies))
                    case _ => None
                }
            case None => None
        }

    def makeMoviePersons(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                         input_movie: MovieMovie, output_movie: MovieMovie): Option[MovieLink] =
        MovieMetamodel.getPersonsOfMovie(model, input_movie) match {
            case Some(persons) =>
                var actors: Option[List[MoviePerson]] = None
                var actress: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTOR, MovieMetamodel.ACTOR, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // Then we get output actresses
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTRESS, MovieMetamodel.ACTRESS, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActress]) => actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new MovieToPersons(output_movie, people))
                }
            case _ => None
        }

    def makeCoupleToPerson(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, person: MoviePerson,
                           couple: MovieCouple, i: Int): Option[DynamicLink] = {
        val pattern = if (person.isInstanceOf[MovieActor]) PATTERN_ACTOR else PATTERN_ACTRESS
        val type_ : String = {
            pattern match {
                case PATTERN_ACTOR => MovieMetamodel.ACTOR
                case PATTERN_ACTRESS => MovieMetamodel.ACTRESS
                case _ => ""
            }
        }
        (Resolve.resolve(tls, model, mm, pattern, type_, List(person)), i) match {
            case (Some(act: MoviePerson), 1) => Some(new CoupleToPersonP1(couple, act))
            case (Some(act: MoviePerson), 2) => Some(new CoupleToPersonP2(couple, act))
            case _ => None
        }
    }

    def findcouples_imdb(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "movie2movie",
                    types = List(MovieMetamodel.MOVIE),
                    from = (m, l) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
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
                    types = List(MovieMetamodel.ACTOR),
                    from = (m, l) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
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
                    types = List(MovieMetamodel.ACTRESS),
                    from = (m, l) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
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
                    name = "couple_actor_person",
                    types = List(MovieMetamodel.ACTOR),
                    from = (_, _) => { my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                    itExpr = (sm, l) => {
                        val model = sm.asInstanceOf[MovieModel]
                        val p0 = l.head.asInstanceOf[MoviePerson]
                        Some(helper_candidate(model, p0).length)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE_ACTOR_PERSON,
                            elementExpr = (i, sm, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val p0 = l.head.asInstanceOf[MovieActor]
                                    val model = sm.asInstanceOf[MovieModel]
                                    val p1 = helper_candidate(model, p0)(i)
                                    if(helper_areCouple(model, p0, p1)){
                                        val common = helper_commonMovies(model.asInstanceOf[MovieModel], p0, p1)
                                        val avgRating = common.map(m => m.getRating).sum / common.size
                                        Some(new MovieCouple(p0.getName + " & " + p1.getName, avgRating))
                                    } else None
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val p0 = pattern.head.asInstanceOf[MoviePerson]
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            p0, output.asInstanceOf[MovieCouple], 1)
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, i, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val p0 = pattern.head.asInstanceOf[MovieActor]
                                        val model = sm.asInstanceOf[MovieModel]
                                        val p1 = helper_candidate(model, p0)(i)
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            p1, output.asInstanceOf[MovieCouple], 2)
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple_actress_person",
                    types = List(MovieMetamodel.ACTRESS),
                    from = (_, _) => { my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                    itExpr = (sm, l) => {
                        val model = sm.asInstanceOf[MovieModel]
                        val p0 = l.head.asInstanceOf[MoviePerson]
                        Some(helper_candidate(model, p0).length)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE_ACTRESS_PERSON,
                            elementExpr = (i, sm, l) =>
                                if (l.isEmpty) None else {
                                    my_sleep(sleeping_instantiate, random.nextInt)
                                    val p0 = l.head.asInstanceOf[MovieActress]
                                    val model = sm.asInstanceOf[MovieModel]
                                    val p1 = helper_candidate(model, p0)(i)
                                    if(helper_areCouple(model, p0, p1)){
                                        val common = helper_commonMovies(model.asInstanceOf[MovieModel], p0, p1)
                                        val avgRating = common.map(m => m.getRating).sum / common.size
                                        Some(new MovieCouple(p0.getName + " & " + p1.getName, avgRating))
                                    } else None
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val p0 = pattern.head.asInstanceOf[MoviePerson]
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            p0, output.asInstanceOf[MovieCouple], 1)
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, i, sm, pattern, output) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val p0 = pattern.head.asInstanceOf[MoviePerson]
                                        val model = sm.asInstanceOf[MovieModel]
                                        val p1 = helper_candidate(model, p0)(i)
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            p1, output.asInstanceOf[MovieCouple], 2)
                                    }
                                )
                            )
                        )
                    )
                )
            )
        )
}
