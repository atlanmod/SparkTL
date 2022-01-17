package org.atlanmod.dblpinfo.model.authorinfo.metamodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait AuthorInfoMetamodel {
    final val AUTHOR = "Author"
    final val JOURNAL = "Journal"
    final val CONFERENCE = "Conference"

    final val COAUTHOR = "coauthor"
    final val PUBLISHESINJ = "publishesInJ"
    final val PUBLISHESINC = "publishesInC"

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("AuthorInfoMetamodel")

}
