package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInCollection, DblpOrganization}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class InCollectionToOrganization(source: DblpInCollection, target: DblpOrganization)
  extends DblpLink(DblpMetamodelNaive.INCOLLECTION_ORGANIZATION, source, List(target)) {

    override def getSource: DblpInCollection = source
    override def getTarget: List[DblpOrganization] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}