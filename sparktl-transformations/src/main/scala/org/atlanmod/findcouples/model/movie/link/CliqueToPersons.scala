package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.{MovieClique, MoviePerson}
import org.atlanmod.findcouples.model.movie.{MovieLink, MovieMetamodel}

class CliqueToPersons (source: MovieClique, target: List[MoviePerson])
  extends MovieLink(MovieMetamodel.CLIQUE_PERSONS, source, target){

    def this(source: MovieClique, target: MoviePerson) =
        this(source, List(target))

    override def getSource: MovieClique = source
    override def getTarget: List[MoviePerson] = target

    override def toString: String = source.toString + ": " + target.mkString(",", "[", "]")
}
