package org.atlanmod.findcouples.model.movie.element

import org.atlanmod.IdGenerator
import org.atlanmod.findcouples.model.movie.metamodel.MovieMetamodelNaive

class MovieActor extends MoviePerson (MovieMetamodelNaive.ACTOR) {

    def this(name: String) = {
        this()
        val id: Long = IdGenerator.id()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    def this(id: Long, name: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("name", name)
    }

    override def getName: String = super.eGetProperty("name").asInstanceOf[String]
    override def getId: Long = eGetProperty("id").asInstanceOf[Long]

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
