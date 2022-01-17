package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.element.{DblpEditor, DblpProceedings}
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class ProceedingsToEditors(source: DblpProceedings, target: List[DblpEditor])
  extends DblpLink(DblpMetamodelNaive.PROCEEDINGS_EDITORS, source, target) {

    def this(source: DblpProceedings, target: DblpEditor) =
        this(source, List(target))

    override def getSource: DblpProceedings = source
    override def getTarget: List[DblpEditor] = target

    override def toString: String = source.toString + "[" + target.mkString(",") + "]"

}