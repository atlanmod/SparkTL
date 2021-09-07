package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.findcouples.model.movie._
import org.atlanmod.findcouples.model.movie.element._
import org.atlanmod.findcouples.model.movie.link.{CoupleToPersonP1, CoupleToPersonP2, MovieToPersons, PersonToMovies}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils


object FindCouples {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_COUPLE_ACTOR_ACTOR = "couple_actor_actor"
    final val PATTERN_COUPLE_ACTRESS_ACTRESS = "couple_actress_actress"
    final val PATTERN_COUPLE_ACTRESS_ACTOR = "couple_actress_actor"

    val mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def helper_coactor(model: MovieModel, p: MoviePerson): List[MoviePerson] =
        MovieMetamodel.getMoviesOfPerson(model, p) match {
            case Some(movies: List[MovieMovie]) =>
                movies.flatMap(movie => MovieMetamodel.getPersonsOfMovieAsList(model, movie))
            case _ => List()
        }

    def helper_areCouple(model: MovieModel, p1: MoviePerson, p2: MoviePerson): Boolean =
        helper_commonMovies(model, p1, p2).size >= 3 & !p1.equals(p2) &
          (if (p1.isInstanceOf[MovieActor] & p2.isInstanceOf[MovieActor]) p1.getName <= p2.getName else true)

    def helper_commonMovies(model: MovieModel, p1: MoviePerson, p2: MoviePerson): List[MovieMovie] =
        (MovieMetamodel.getMoviesOfPerson(model, p1), MovieMetamodel.getMoviesOfPerson(model, p2)) match {
            case (Some(movies1), Some(movies2)) => movies1.intersect(movies2)
            case _ => List()
        }

    def makePersonMovies (tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel,
                          input_person: MoviePerson, output_person: MoviePerson): Option[MovieLink] =
        MovieMetamodel.getMoviesOfPerson(model, input_person) match {
            case Some(movies) =>
                Resolve.resolveAll(tls, model, mm, PATTERN_MOVIE, MovieMetamodel.MOVIE, ListUtils.listToListList(movies)) match {
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
                var actress: Option[List[MoviePerson]] = None
                // First we get output actors
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTOR, MovieMetamodel.ACTOR, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActor]) => actors = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // Then we get output actresses
                Resolve.resolveAll(tls, model, mm, PATTERN_ACTRESS, MovieMetamodel.ACTRESS, ListUtils.singletons(persons)) match {
                    case Some(l_act: List[MovieActress]) =>  actress = Some(l_act.asInstanceOf[List[MoviePerson]])
                }
                // We make the sum actors + actresses
                ListUtils.sum_list_option(actors, actress) match {
                    case Some(people: List[MoviePerson]) => Some(new MovieToPersons(output_movie, people))
                }
            }
            case _ => None
        }

    def makeCoupleToPerson(tls: TraceLinks[DynamicElement, DynamicElement], model: MovieModel, person: MoviePerson,
                           couple: MovieCouple, pattern: String, i: Int): Option[DynamicLink] = {
        val type_ : String = {
            pattern match {
                case PATTERN_ACTOR => MovieMetamodel.ACTOR
                case PATTERN_ACTRESS => MovieMetamodel.ACTRESS
                case _ => ""
            }}
        (Resolve.resolve(tls, model, mm, pattern, type_, List(person)), i) match {
            case (Some(act: MoviePerson), 1) => Some(new CoupleToPersonP1(couple, act))
            case (Some(act: MoviePerson), 2) => Some(new CoupleToPersonP2(couple, act))
            case _ => None
        }
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
                                    Some(new MovieActress(actress.getName))
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
                ),
                new RuleImpl(
                    name = "couple_actor_actor",
                    types = List(MovieMetamodel.ACTOR, MovieMetamodel.ACTOR),
                    from = (model, pattern) => {
                        val p1 = pattern.head.asInstanceOf[MovieActor]
                        val p2 = pattern(1).asInstanceOf[MovieActor]
                        Some(helper_areCouple(model.asInstanceOf[MovieModel], p1, p2))
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE_ACTOR_ACTOR,
                            elementExpr = (_, model, l) =>
                              if (l.size < 2) None else {
                                  val p1 = l.head.asInstanceOf[MovieActor]
                                  val p2 = l(1).asInstanceOf[MovieActor]
                                  val movies = helper_commonMovies(model.asInstanceOf[MovieModel], p1, p2)
                                  val avgRating = movies.map(m => m.getRating).sum / movies.size
                                  Some(new MovieCouple(p1.getName + " & " +  p2.getName, avgRating))
                              },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActor], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTOR, 1)
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern(1).asInstanceOf[MovieActor], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTOR, 2)
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple_actress_actor",
                    types = List(MovieMetamodel.ACTRESS, MovieMetamodel.ACTOR),
                    from = (model, pattern) => {
                        val p1 = pattern.head.asInstanceOf[MovieActress]
                        val p2 = pattern(1).asInstanceOf[MovieActor]
                        Some(helper_areCouple(model.asInstanceOf[MovieModel], p1, p2))
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE_ACTRESS_ACTOR,
                            elementExpr = (_, model, l) =>
                                if (l.size < 2) None else {
                                    val p1 = l.head.asInstanceOf[MovieActress]
                                    val p2 = l(1).asInstanceOf[MovieActor]
                                    val movies = helper_commonMovies(model.asInstanceOf[MovieModel], p1, p2)
                                    val avgRating = movies.map(m => m.getRating).sum / movies.size
                                    Some(new MovieCouple(p1.getName + " & " +  p2.getName, avgRating))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActress], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTRESS, 1)
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern(1).asInstanceOf[MovieActor], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTOR, 2)
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple_actress_actress",
                    types = List(MovieMetamodel.ACTRESS, MovieMetamodel.ACTRESS),
                    from = (model, pattern) => {
                        val p1 = pattern.head.asInstanceOf[MovieActress]
                        val p2 = pattern(1).asInstanceOf[MovieActress]
                        Some(helper_areCouple(model.asInstanceOf[MovieModel], p1, p2))
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE_ACTRESS_ACTRESS,
                            elementExpr = (_, model, l) =>
                                if (l.size < 2) None else {
                                    val p1 = l.head.asInstanceOf[MovieActress]
                                    val p2 = l(1).asInstanceOf[MovieActress]
                                    val movies = helper_commonMovies(model.asInstanceOf[MovieModel], p1, p2)
                                    val avgRating = movies.map(m => m.getRating).sum / movies.size
                                    Some(new MovieCouple(p1.getName + " & " +  p2.getName, avgRating))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern.head.asInstanceOf[MovieActress], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTRESS, 1)
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, pattern, output) =>
                                        makeCoupleToPerson(tls, sm.asInstanceOf[MovieModel],
                                            pattern(1).asInstanceOf[MovieActress], output.asInstanceOf[MovieCouple],
                                            PATTERN_ACTRESS, 2)
                                )
                            )
                        )
                    )
                )
            )
        )

}
