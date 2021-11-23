package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.Model

class DefaultModel[SME, SML] (elements: List[SME], links: List[SML]) extends Model[SME, SML] {

    def this(elements:Iterable[SME], links:Iterable[SML]) = {
        this(elements.toList, links.toList)
    }

    override def allModelElements: List[SME] = elements
    override def allModelLinks: List[SML] = links
}
