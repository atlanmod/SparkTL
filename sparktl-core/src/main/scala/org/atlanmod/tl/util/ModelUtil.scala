package org.atlanmod.tl.util

import org.atlanmod.tl.model.Model

object ModelUtil {

    def makeTupleModel[ME, ML](elements: Iterable[ME], links: Iterable[ML]): Model[ME, ML] = {
        class tupleModel(elements: Iterable[ME], links: Iterable[ML]) extends Model[ME, ML] {
            override def allModelElements: List[ME] = elements.toList
            override def allModelLinks: List[ML] = links.toList
        }
        new tupleModel(elements, links)
    }

}
