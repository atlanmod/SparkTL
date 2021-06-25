package org.atlanmod.util

import org.atlanmod.class2relational.model.relationalmodel.{RelationalElement, RelationalLink, RelationalModel}

object R2CUtil {

    def get_model_from_n_patterns(n: Int): RelationalModel = {
        var elements : List[RelationalElement] = List()
        var links  : List[RelationalLink] = List()
        for (_ <- 0 until n) {
            val tmp_model = org.atlanmod.class2relational.model.ModelSamples.getRelationalModelSample
            elements = elements ++ tmp_model.allModelElements
            links = links ++ tmp_model.allModelLinks
        }
        new RelationalModel(elements, links)
    }

}
