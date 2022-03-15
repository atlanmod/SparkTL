package org.atlanmod.ttc18.model.socialnetwork.element

import java.util.Date

import org.atlanmod.ttc18.model.socialnetwork.SocialNetworkElement

abstract class SocialNetworkSubmission(classname: String) extends SocialNetworkElement (classname: String){
    def getScore: Int
    def getContent: String
    def getTimestamp: Date
}