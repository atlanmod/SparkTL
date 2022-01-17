package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpEditor, DblpWww}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class WwwToEditors(source: DblpWww, target: List[DblpEditor])
  extends DblpLink(DblpMetamodelNaive.WWW_EDITORS, source, target) {

    def this(source: DblpWww, target: DblpEditor) =
        this(source, List(target))

    override def getSource: DblpWww = source
    override def getTarget: List[DblpEditor] = target

    override def toString: String = source.toString + "[" + target.mkString(",") + "]"

}