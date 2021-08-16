package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInCollection, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InCollectionToPublisher(source: DblpInCollection, target: DblpPublisher)
  extends DblpLink(DblpMetamodel.INCOLLECTION_PUBLISHER, source, List(target)) {

    override def getSource: DblpInCollection = source
    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}