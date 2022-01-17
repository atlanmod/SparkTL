package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpRecord}
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class AuthorToRecords (source: DblpAuthor, target: List[DblpRecord])
  extends DblpLink(DblpMetamodelNaive.AUTHOR_RECORDS, source, target) {

    def this(source: DblpAuthor, target: DblpRecord) =
        this(source, List(target))


    override def getSource: DblpAuthor = source
    override def getTarget: List[DblpRecord] = target

    override def toString: String = source.toString + "'s publications: \n" + target.mkString("","\n","")

}