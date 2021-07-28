package org.atlanmod.findcouples.model.movie

class MovieToPersons(source: MovieMovie, target: List[MoviePerson])
  extends MovieLink(MovieMetamodel.MOVIE_PERSONS, source, target){

    def this(source: MovieMovie,  target: List[MoviePerson]) = {
        this(source, target)
    }

    override def getSource: MovieMovie = source
    override def getTarget: List[MoviePerson] = target

    override def toString: String = source.getTitle + ": " + target.mkString(",", "[", "]")

}
