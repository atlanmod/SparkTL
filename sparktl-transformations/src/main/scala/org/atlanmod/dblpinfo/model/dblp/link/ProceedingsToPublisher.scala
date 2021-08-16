package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpProceedings, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class ProceedingsToPublisher(source: DblpProceedings, target: DblpPublisher)
  extends DblpLink(DblpMetamodel.PROCEEDINGS_PUBLISHER, source, List(target)) {

    override def getSource: DblpProceedings = source

    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}