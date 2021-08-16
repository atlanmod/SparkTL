package org.atlanmod.findcouples.model.movie

class CliqueToPersons (source: MovieClique, target: List[MoviePerson])
  extends MovieLink(MovieMetamodel.CLIQUE_PERSONS, source, target){

    override def getSource: MovieClique = source
    override def getTarget: List[MoviePerson] = target

    override def toString: String = source.toString + ": " + target.mkString(",", "[", "]")
}
