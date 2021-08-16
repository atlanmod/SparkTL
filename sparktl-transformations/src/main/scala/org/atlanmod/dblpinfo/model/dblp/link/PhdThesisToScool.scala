package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpPhdThesis, DblpSchool}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class PhdThesisToScool(source: DblpPhdThesis, target: DblpSchool)
  extends DblpLink(DblpMetamodel.PHDTHESIS_SCHOOL, source, List(target)) {

    override def getSource: DblpPhdThesis = source
    override def getTarget: List[DblpSchool] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}