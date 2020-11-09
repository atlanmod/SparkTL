package org.atlanmod.tl.model

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.common.util.TreeIterator

trait Model[ME, ML] {
    /*
    *  ME: ModelElements
    *  ML: ModelLinks
    */
    def allModelElements: List[ME]

    def allModelLinks: List[ML]
}

class EMFModel(r: EObject) extends Model[EObject, ELink] {

    def allModelElements: List[EObject] = {
        var l: List[EObject] = List()
        var i = r.eAllContents()
        while (i.hasNext) {
          l = i.next() :: l
        }
        l
    }

    def allModelLinks: List[ELink] = {
        // TODO
        List()
    }

}
