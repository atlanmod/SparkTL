package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.DblpElement

abstract class DblpRecord (classname: String) extends DblpElement(classname) {
    def getEe: String
    def getUrl: String
    def getKey: String
    def getMdate: String
}
