package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class RecordToAuthors(source: DblpRecord, target: List[DblpAuthor])
  extends DblpLink(DblpMetamodel.RECORD_AUTHORS, source, target) {

    def this(source: DblpRecord, target: DblpAuthor) =
        this(source, List(target))


    override def getSource: DblpRecord = source
    override def getTarget: List[DblpAuthor] = target

    override def toString: String = target.mkString(",") + source.toString

}