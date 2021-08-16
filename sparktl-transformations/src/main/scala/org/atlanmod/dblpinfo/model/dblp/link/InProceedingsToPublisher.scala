package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpInProceedings, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InProceedingsToPublisher(source: DblpInProceedings, target: DblpPublisher)
  extends DblpLink(DblpMetamodel.INPROCEEDINGS_PUBLISHER, source, List(target)) {

    override def getSource: DblpInProceedings = source
    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}