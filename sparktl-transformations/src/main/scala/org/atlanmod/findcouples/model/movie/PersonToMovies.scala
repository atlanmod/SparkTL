package org.atlanmod.findcouples.model.movie

class PersonToMovies(source: MoviePerson, target: List[MovieMovie])
  extends MovieLink(MovieMetamodel.PERSON_MOVIES, source, target){

    override def getSource: MoviePerson = source
    override def getTarget: List[MovieMovie] = target

    override def toString: String = source.getName + ": " + target.map(t => t.getTitle).mkString(",", "[", "]")
}
