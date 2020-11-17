package org.atlanmod

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject


class EMFModel(resource: EObject) extends Model[EObject, ELink] {

    def allModelElements: List[EObject] = {
        var res: List[EObject] = List()
        val i = resource.eAllContents()
        while (i.hasNext) {
            res = i.next() :: res
        }
        res
    }

    def allModelLinks: List[ELink] = {
        // TODO
        ???
    }

}

