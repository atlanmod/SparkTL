package org.atlanmod.dblpinfo.model.authorinfo

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

object AuthorInfoMetamodel {

    final val AUTHOR = "Author"
    final val JOURNAL = "Journal"
    final val CONFERENCE = "Conference"

    final val COAUTHOR = "coauthor"
    final val PUBLISHESINJ = "publishesInJ"
    final val PUBLISHESINC = "publishesInC"
    
    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]()

}
