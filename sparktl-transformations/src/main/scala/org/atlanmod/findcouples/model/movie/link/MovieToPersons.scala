package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.{MovieMovie, MoviePerson}
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodel}

class MovieToPersons(source: MovieMovie, target: List[MoviePerson])
  extends MovieLink(MovieMetamodel.MOVIE_PERSONS, source, target){

    override def getSource: MovieMovie = source
    override def getTarget: List[MoviePerson] = target

    override def toString: String = source.getTitle + ": " + target.mkString(",", "[", "]")

}
