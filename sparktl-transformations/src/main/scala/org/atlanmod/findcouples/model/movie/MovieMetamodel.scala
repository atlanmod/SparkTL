package org.atlanmod.findcouples.model.movie

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object MovieMetamodel {

    final val MOVIE = "Movie"
    final val GROUP = "Group"
    final val COUPLE = "Couple"
    final val CLIQUE = "Clique"
    final val PERSON = "Person"
    final val ACTOR = "Actor"
    final val ACTRESS = "Actress"

    final val CLIQUE_PERSONS: String = "persons"
    final val COUPLE_PERSON_P1: String = "p1"
    final val COUPLE_PERSON_P2: String = "p2"
    final val GROUP_MOVIES: String = "commonMovies"
    final val PERSON_MOVIES: String = "movies"
    final val MOVIE_PERSONS: String = "persons"

    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink]
    = new DynamicMetamodel[DynamicElement, DynamicLink]()

}
