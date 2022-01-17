package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInCollection, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class InCollectionToPublisher(source: DblpInCollection, target: DblpPublisher)
  extends DblpLink(DblpMetamodelNaive.INCOLLECTION_PUBLISHER, source, List(target)) {

    override def getSource: DblpInCollection = source
    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}