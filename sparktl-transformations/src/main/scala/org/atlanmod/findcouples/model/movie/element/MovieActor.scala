package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.findcouples.model.movie.MovieMetamodel

class MovieActor extends MoviePerson (MovieMetamodel.ACTOR) {

    def this(name: String) = {
        this()
        super.eSetProperty("name", name)
    }

    override def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

    override def equals(o: Any): Boolean = {
        o match {
            case obj: MovieActor =>
                this.getName.equals(obj.getName)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = equals(o)

}
