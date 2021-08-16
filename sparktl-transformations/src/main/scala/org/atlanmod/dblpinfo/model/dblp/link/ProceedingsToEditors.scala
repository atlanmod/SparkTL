package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}
import org.atlanmod.dblpinfo.model.dblp.element.{DblpEditor, DblpProceedings}

class ProceedingsToEditors(source: DblpProceedings, target: List[DblpEditor])
  extends DblpLink(DblpMetamodel.PROCEEDINGS_EDITORS, source, target) {

    def this(source: DblpProceedings, target: DblpEditor) =
        this(source, List(target))

    override def getSource: DblpProceedings = source
    override def getTarget: List[DblpEditor] = target

    override def toString: String = source.toString + "[" + target.mkString(",") + "]"

}