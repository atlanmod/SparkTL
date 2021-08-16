package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpOrganization, DblpProceedings}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class ProceedingsToOrganization(source: DblpProceedings, target: DblpOrganization)
  extends DblpLink(DblpMetamodel.PROCEEDINGS_ORGANIZATION, source, List(target)) {

    override def getSource: DblpProceedings = source

    override def getTarget: List[DblpOrganization] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"
}