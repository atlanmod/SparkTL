package org.atlanmod.dblpinfo.model.dblp.element

import org.atlanmod.dblpinfo.model.dblp.DblpElement

abstract class DblpRecord (classname: String) extends DblpElement(classname) {
    def getEe: String
    def getUrl: String
    def getKey: String
    def getMdate: String

    override def equals(o: Any): Boolean = o match {
        case obj: DblpRecord =>
            obj.getEe.equals(getEe) & obj.getUrl.equals(getUrl) &
              obj.getKey.equals(getKey) & obj.getMdate.equals(getMdate)
        case _ => false
    }

}
