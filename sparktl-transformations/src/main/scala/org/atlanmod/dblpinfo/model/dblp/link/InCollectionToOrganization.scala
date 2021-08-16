package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInCollection, DblpOrganization}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InCollectionToOrganization(source: DblpInCollection, target: DblpOrganization)
  extends DblpLink(DblpMetamodel.INCOLLECTION_ORGANIZATION, source, List(target)) {

    override def getSource: DblpInCollection = source
    override def getTarget: List[DblpOrganization] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}