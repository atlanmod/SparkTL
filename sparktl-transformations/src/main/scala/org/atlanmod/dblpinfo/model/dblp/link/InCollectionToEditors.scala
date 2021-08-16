package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpEditor, DblpInCollection}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class InCollectionToEditors(source: DblpInCollection, target: List[DblpEditor])
  extends DblpLink(DblpMetamodel.INCOLLECTION_EDITORS, source, target) {

    def this(source: DblpInCollection, target: DblpEditor) =
        this(source, List(target))

    override def getSource: DblpInCollection = source
    override def getTarget: List[DblpEditor] = target

    override def toString: String = source.toString + "[" + target.mkString(",") + "]"

}