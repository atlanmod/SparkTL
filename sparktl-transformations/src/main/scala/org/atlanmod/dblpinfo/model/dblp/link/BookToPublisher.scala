package org.atlanmod.dblpinfo.model.dblp.link

import org.atlanmod.dblpinfo.model.dblp.element.{DblpBook, DblpPublisher}
import org.atlanmod.dblpinfo.model.dblp.{DblpLink, DblpMetamodel}

class BookToPublisher(source: DblpBook, target: DblpPublisher)
  extends DblpLink(DblpMetamodel.BOOK_PUBLISHER, source, List(target)) {

    override def getSource: DblpBook = source
    override def getTarget: List[DblpPublisher] = List(target)

    override def toString: String = source.toString + "(" + target.toString + ")"

}