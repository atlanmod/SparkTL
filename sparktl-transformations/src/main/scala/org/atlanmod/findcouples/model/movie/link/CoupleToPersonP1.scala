package org.atlanmod.findcouples.model.movie.link

import org.atlanmod.findcouples.model.movie.element.MovieCouple
import org.atlanmod.findcouples.model.movie.MovieLink
import org.atlanmod.findcouples.model.movie.MovieLink
import org.atlanmod.findcouples.model.movie.element.{MovieCouple, MoviePerson}
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodelNaive

class CoupleToPersonP1 (source: MovieCouple,  target: MoviePerson)
  extends MovieLink(MovieMetamodelNaive.COUPLE_PERSON_P1, source, List(target)){

    override def getSource: MovieCouple = source
    override def getTarget: List[MoviePerson] = List(target)

    def getPersonP1: MoviePerson = target

    override def toString: String = source.toString + ": " + target.getName
}
