package org.atlanmod.findcouples.model.movie

class MovieActor extends MoviePerson (MovieMetamodel.ACTOR) {

    def this(name: String) = {
        this()
        super.eSetProperty("name", name)
    }

    override def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def toString: String = getName

}