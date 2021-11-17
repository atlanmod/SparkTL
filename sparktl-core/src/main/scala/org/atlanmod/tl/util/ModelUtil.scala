package org.atlanmod.tl.util

import org.atlanmod.tl.model.Model

object ModelUtil {

    def makeTupleModel[ME, ML, MC](elements: Iterable[ME], links: Iterable[ML]): Model[ME, ML, MC] = {
        class tupleModel(elements: Iterable[ME], links: Iterable[ML]) extends Model[ME, ML, MC] {
            override def allModelElements: Iterator[ME] = elements.iterator
            override def allModelLinks: Iterator[ML] = links.iterator
            override def allElementsOfType(cl: MC): Iterator[ME] = elements.iterator // TODO
        }
        new tupleModel(elements, links)
    }

}
