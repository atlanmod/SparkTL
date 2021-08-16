package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpMastersThesis, DblpSchool}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class MastersThesisToScool(source: DblpMastersThesis, target: DblpSchool)
  extends DblpLink(DblpMetamodel.MASTERSTHESIS_SCHOOL, source, List(target)) {

    override def getSource: DblpMastersThesis = source
    override def getTarget: List[DblpSchool] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}