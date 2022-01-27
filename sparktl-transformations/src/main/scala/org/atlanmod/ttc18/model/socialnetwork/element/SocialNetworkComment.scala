package org.atlanmod.ttc18.model.socialnetwork.element

import java.util.Date

import org.atlanmod.ttc18.model.socialnetwork.metamodel.SocialNetworkMetamodelNaive

class SocialNetworkComment extends SocialNetworkSubmission(SocialNetworkMetamodelNaive.COMMENT){

    def this(id: String, timestamp: Date, content: String) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("timestamp", timestamp)
        super.eSetProperty("content", content)
        super.eSetProperty("score", 0)
    }

    def this(id: String, timestamp: Date, content: String, score: Int) = {
        this()
        super.eSetProperty("id", id)
        super.eSetProperty("timestamp", timestamp)
        super.eSetProperty("content", content)
        super.eSetProperty("score", score)
    }

    def setId(id: Int) = {
        super.eSetProperty("id", id)
    }

    def setTimestamp(timestamp: Date) = {
        super.eSetProperty("timestamp", timestamp)
    }

    def setContent(content: String) = {
        super.eSetProperty("content", content)
    }

    def setScore(score: Int) = {
        super.eSetProperty("score", score)
    }

    override def getId: String = super.eGetProperty("id").asInstanceOf[String]
    override def getScore: Int = super.eGetProperty("score").asInstanceOf[Int]
    override def getContent: String = super.eGetProperty("content").asInstanceOf[String]
    override def getTimestamp: Date = super.eGetProperty("timestamp").asInstanceOf[Date]

    override def equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkComment => this.getId.equals(obj.getId) & this.getContent.equals(obj.getContent) &
              this.getTimestamp.equals(obj.getTimestamp) &
              this.getScore.equals(obj.getScore)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: SocialNetworkComment  => this.getContent.equals(obj.getContent) &
                this.getTimestamp.equals(obj.getTimestamp) & this.getScore.equals(obj.getScore)
            case _ => false
        }
    }
}
