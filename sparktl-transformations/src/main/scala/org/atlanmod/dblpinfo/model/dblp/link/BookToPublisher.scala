package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpBook, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.DblpLink
import org.atlanmod.dblpinfo.model.dblp.metamodel.DblpMetamodelNaive

class BookToPublisher(source: DblpBook, target: DblpPublisher)
  extends DblpLink(DblpMetamodelNaive.BOOK_PUBLISHER, source, List(target)) {

    override def getSource: DblpBook = source
    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}