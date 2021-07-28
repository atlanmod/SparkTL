package org.atlanmod.findcouples.model.movie

class CoupleToPersonP1 (source: MovieCouple,  target: MoviePerson)
  extends MovieLink(MovieMetamodel.COUPLE_PERSON_P1, source, List(target)){

    override def getSource: MovieCouple = source
    override def getTarget: List[MoviePerson] = List(target)

    override def toString: String = source.toString + ": " + target.getName
}
