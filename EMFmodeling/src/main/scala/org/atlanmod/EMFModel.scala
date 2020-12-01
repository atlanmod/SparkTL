package org.atlanmod

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.{EObject, EReference}


class EMFModel(resource: Resource) extends Model[EObject, ELink] {

    def allModelElements: List[EObject] = {
        var res: List[EObject] = resource.getContents.toArray().toList.map(a => a.asInstanceOf[EObject])
//        val i = resource.getAllContents
//        while (i.hasNext) {
//            res = i.next() :: res
//        }
        res
    }

    def allModelLinks: List[ELink] = {
        var res: List[ELink] = List()
        for (source <- allModelElements){
            val allRef = source.eClass().getEAllReferences
            for (ref : EReference <- allRef.asInstanceOf[List[EReference]]){
                res = new ELink(source, ref, source.eGet(ref)) :: res
            }
        }
        res
    }

}

