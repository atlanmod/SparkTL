package org.atlanmod.tl.util

import org.atlanmod.tl.model.Model

object ModelUtil {

    def makeTupleModel[ME, ML](elements: List[ME],links: List[ML]): Model[ME, ML] = {
        class tupleModel(elements: List[ME], links: List[ML]) extends Model[ME, ML] {
            override def allModelElements: List[ME] = elements
            override def allModelLinks: List[ML] = links
        }
        new tupleModel(elements, links)
    }

}
