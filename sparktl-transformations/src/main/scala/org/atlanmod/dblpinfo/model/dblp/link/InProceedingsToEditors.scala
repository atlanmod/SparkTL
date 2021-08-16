package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpEditor, DblpInProceedings}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InProceedingsToEditors(source: DblpInProceedings, target: List[DblpEditor])
  extends DblpLink(DblpMetamodel.PROCEEDINGS_EDITORS, source, target) {

    def this(source: DblpInProceedings, target: DblpEditor) =
        this(source, List(target))
    
    override def getSource: DblpInProceedings = source
    override def getTarget: List[DblpEditor] = target

    override def toString: String = source.toString + "[" + target.mkString(",") + "]"

}