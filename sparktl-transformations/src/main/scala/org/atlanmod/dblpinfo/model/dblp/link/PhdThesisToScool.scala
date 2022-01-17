package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpPhdThesis, DblpSchool}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class PhdThesisToScool(source: DblpPhdThesis, target: DblpSchool)
  extends DblpLink(DblpMetamodelNaive.PHDTHESIS_SCHOOL, source, List(target)) {

    override def getSource: DblpPhdThesis = source
    override def getTarget: List[DblpSchool] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}