package org.atlanmod.ttc18.model.socialnetwork.element

import org.atlanmod.IdGenerator
import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkElement
import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class SocialNetworkUser extends SocialNetworkElement(SocialNetworkMetamodelNaive.USER){

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

    @deprecated
    def setName(name: String) = {
        super.eSetProperty("name", name)
    }

    override def getId: Long = super.eGetProperty("id").asInstanceOf[Long]
    def getName: String = super.eGetProperty("name").asInstanceOf[String]

    override def equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkUser => this.getId.equals(obj.getId) & this.getName.equals(obj.getName)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkUser  => this.getName.equals(obj.getName)
            case _ => false
        }
    }
}