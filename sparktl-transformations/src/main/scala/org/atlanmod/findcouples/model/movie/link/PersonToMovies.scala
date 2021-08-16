package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.{MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodel}

class PersonToMovies(source: MoviePerson, target: List[MovieMovie])
  extends MovieLink(MovieMetamodel.PERSON_MOVIES, source, target){

    def this(source: MoviePerson, target: MovieMovie) =
        this(source, List(target))

    override def getSource: MoviePerson = source
    override def getTarget: List[MovieMovie] = target

    override def toString: String = source.getName + ": " + target.map(t => t.getTitle).mkString(",", "[", "]")
}
