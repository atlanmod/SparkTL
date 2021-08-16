package org.atlanmod.findcouples.model.movie

class GroupToMovies(source: MovieGroup, target: List[MovieMovie])
  extends MovieLink(MovieMetamodel.GROUP_MOVIES, source, target){

    def this(source: MovieClique,  target: List[MovieMovie]) = {
        this(source, target)
    }
    def this(source: MovieCouple,  target: List[MovieMovie]) = {
        this(source, target)
    }

    override def getSource: MovieGroup = source
    override def getTarget: List[MovieMovie] = target

    override def toString: String = source.toString + ": " + target.mkString(",", "[", "]")
}