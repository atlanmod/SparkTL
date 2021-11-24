package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.MoviePerson
import org.atlanmod.findcouples.model.movie.MovieLink
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodel}
import org.atlanmod.findcouples.model.movie.element.{MovieMovie, MoviePerson}

class MovieToPersons(source: MovieMovie, target: List[MoviePerson])
  extends MovieLink(MovieMetamodel.MOVIE_PERSONS, source, target){

    override def getSource: MovieMovie = source
    override def getTarget: List[MoviePerson] = target

    override def toString: String = source.getTitle + ": " + target.mkString(",", "[", "]")

}
