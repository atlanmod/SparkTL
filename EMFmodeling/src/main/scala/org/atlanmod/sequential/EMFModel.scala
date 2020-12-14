package org.atlanmod.sequential

import org.atlanmod.ELink
import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource

class EMFModel(res: Resource) extends Model[EObject, ELink] {

    var elements: List[EObject] = _
    var links : List[ELink] = _
    var resource: Resource = _

    { // Constructing
        resource = res
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

    def allModelElements: List[EObject] = elements

    def allModelLinks: List[ELink] = links

    override def toString: String = {
        val str_builder = new StringBuilder("")
        str_builder.append("elements (size="+ elements.size +"):\n")
        for (obj : EObject <- elements)
            str_builder.append("\t" + obj.toString + "\n")
        str_builder.append("\n")
        str_builder.append("links (size="+ links.size +"):\n")
        for (link : ELink <- links) {
            str_builder.append("\t link \n")
            str_builder.append(link.toString(ntab = 2))
        }
        str_builder.toString()
    }

}

