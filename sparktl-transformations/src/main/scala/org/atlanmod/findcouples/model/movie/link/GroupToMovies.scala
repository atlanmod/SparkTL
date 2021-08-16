package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.{MovieGroup, MovieMovie}
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodel}

class GroupToMovies(source: MovieGroup, target: List[MovieMovie])
  extends MovieLink(MovieMetamodel.GROUP_MOVIES, source, target){

    def this(source: MovieGroup,  target: MovieMovie) = {
        this(source, List(target))
    }

    override def getSource: MovieGroup = source
    override def getTarget: List[MovieMovie] = target

    override def toString: String = source.toString + ": " + target.mkString(",", "[", "]")
}