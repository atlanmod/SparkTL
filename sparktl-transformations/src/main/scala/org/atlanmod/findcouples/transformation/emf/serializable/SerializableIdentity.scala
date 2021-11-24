package org.atlanmod.findcouples.transformation.emf.serializable

import movies.{Movie, MoviesPackage}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.emf.serializable.naive.{SerializableEClass, SerializableELink, SerializableEObject}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}

object SimpleIdentity {

    final val PATTERN_MOVIE = "movie"
    final val pack = MoviesPackage.eINSTANCE
    final val factory = pack.getMoviesFactory


    def identity_imdb(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[SerializableEObject, SerializableELink, SerializableEClass, SerializableEObject, SerializableELink] = {
        new TransformationImpl[SerializableEObject, SerializableELink, SerializableEClass, SerializableEObject, SerializableELink](
            List(
                new RuleImpl(name = "movie2movie",
                    types = List(new SerializableEClass(pack.getMovie)),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MOVIE,
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None else {
                                    val movie = l.head.asInstanceOf[Movie]
                                    val new_movie = factory.createMovie()
                                    new_movie.setTitle(movie.getTitle)
                                    new_movie.setType(movie.getType)
                                    new_movie.setYear(movie.getYear)
                                    new_movie.setRating(movie.getRating)
                                    Some(new SerializableEObject(new_movie))
                                }
                        )
                    )
                )
            )
        )
    }

}
