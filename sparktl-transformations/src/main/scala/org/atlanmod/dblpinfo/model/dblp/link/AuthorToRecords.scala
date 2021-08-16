package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}
import org.atlanmod.dblpinfo.model.dblp.element.{DblpAuthor, DblpRecord}

class AuthorToRecords (source: DblpAuthor, target: List[DblpRecord])
  extends DblpLink(DblpMetamodel.AUTHOR_RECORDS, source, target) {

    def this(source: DblpAuthor, target: DblpRecord) =
        this(source, List(target))


    override def getSource: DblpAuthor = source
    override def getTarget: List[DblpRecord] = target

    override def toString: String = source.toString + "'s publications: \n" + target.mkString("","\n","")

}