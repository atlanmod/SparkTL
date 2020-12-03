package org.atlanmod

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource

class EMFModel(resource: Resource) extends Model[EObject, ELink] {

    var elements: List[EObject] = null
    var links : List[ELink] = null

    private def initialize() = {
        if (elements == null | links == null){
            elements = List() // init
            links = List() // init
            // Iteration on elements
            val itr_content = resource.getAllContents
            while(itr_content.hasNext) {
                val e = itr_content.next()
                // Add to elements
                elements = e :: elements
                // Add element's links
                val itr_reference = e.eClass().getEAllReferences.iterator()
                while(itr_reference.hasNext){
                    val reference = itr_reference.next()
                    val link = new ELink(e, reference, e.eGet(reference))
                    links = link :: links
                }
            }
        }
    }

    def allModelElements: List[EObject] = {
        initialize()
        elements
    }

    def allModelLinks: List[ELink] = {
        initialize()
        links
    }

}

