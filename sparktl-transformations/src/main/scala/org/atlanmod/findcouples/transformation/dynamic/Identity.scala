package org.atlanmod.findcouples.transformation.dynamic

import org.atlanmod.findcouples.model.movie.{MovieActor, MovieActress, MovieClique, MovieCouple, MovieMetamodel, MovieMovie}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}

object Identity {

    final val PATTERN_MOVIE = "movie"
    final val PATTERN_ACTOR = "actor"
    final val PATTERN_ACTRESS = "actress"
    final val PATTERN_CLIQUE = "clique"
    final val PATTERN_COUPLE = "couple"

    def identity_imdb: Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
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
                            outputElemRefs = List(/* TODO MovieToPersons */)
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
                            outputElemRefs = List(/* TODO PersonToMovie */)
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
                            outputElemRefs = List(/* TODO PersonToMovie */)
                        )
                    )
                ),
                new RuleImpl(
                    name = "clique",
                    types = List(MovieMetamodel.CLIQUE),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_CLIQUE,
                            elementExpr = (_,_,l) =>
                                if (l.isEmpty) None else {
                                    val clique = l.head.asInstanceOf[MovieClique]
                                    Some(new MovieClique(clique.getAvgRating))
                                },
                            outputElemRefs = List(/* TODO GroupeToMovies and CliqueToPersons  */)
                        )
                    )
                ),
                new RuleImpl(
                    name = "couple",
                    types = List(MovieMetamodel.COUPLE),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_COUPLE,
                            elementExpr = (_,_,l) =>
                                if (l.isEmpty) None else {
                                    val couple = l.head.asInstanceOf[MovieCouple]
                                    Some(new MovieCouple(couple.getAvgRating))
                                },
                            outputElemRefs = List(/* TODO GroupeToMovies, CoupleToPersonP1, and CoupleToPersonP2 */)
                        )
                    )
                )
            )
        )

}
