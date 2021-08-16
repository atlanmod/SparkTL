package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInProceedings, DblpOrganization}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InProceedingsToOrganization(source: DblpInProceedings, target: DblpOrganization)
  extends DblpLink(DblpMetamodel.INPROCEEDINGS_ORGANIZATION, source, List(target)) {

    override def getSource: DblpInProceedings = source
    override def getTarget: List[DblpOrganization] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}