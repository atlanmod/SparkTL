package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.Model

class DefaultModel[ME, ML](elements: Iterable[ME], links: Iterable[ML]) extends Model[ME, ML]{
    override def allModelElements: List[ME] = elements.toList
    override def allModelLinks: List[ML] = links.toList
    def elements_size(): Int = elements.size
    def links_size(): Int = links.size
}