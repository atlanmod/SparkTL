package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class RecordToAuthors(source: DblpRecord, target: List[DblpAuthor])
  extends DblpLink(DblpMetamodelNaive.RECORD_AUTHORS, source, target) {

    def this(source: DblpRecord, target: DblpAuthor) =
        this(source, List(target))


    override def getSource: DblpRecord = source
    override def getTarget: List[DblpAuthor] = target

    override def toString: String = target.mkString(",") + source.toString

}