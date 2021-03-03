package org.atlanmod.util

import org.atlanmod.model.classmodel.{ClassElement, ClassLink, ClassModel}

object C2RUtil {

    def get_model_from_n_patterns(n: Int): ClassModel = {
        var elements : List[ClassElement] = List()
        var links  : List[ClassLink] = List()
        for (_ <- 0 to n) {
            val tmp_model = org.atlanmod.model.ModelSamples.getClassModelSample
            elements = elements ++ tmp_model.allModelElements
            links = links ++ tmp_model.allModelLinks
        }
        new ClassModel(elements, links)
    }

}
