package org.atlanmod.parallel

import org.atlanmod.ELink
import org.atlanmod.tl.model.Model
import org.atlanmod.wrapper.{ELinkWrapper, EObjectWrapper}
import org.eclipse.emf.ecore.resource.Resource

class EMFModelSerializable(res: Resource) extends Model[EObjectWrapper, ELinkWrapper]  {

    var elements: List[EObjectWrapper] = _
    var links : List[ELinkWrapper] = _
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
                elements = new EObjectWrapper(e) :: elements
                // Add element's links
                val itr_reference = e.eClass().getEAllReferences.iterator()
                while(itr_reference.hasNext){
                    val reference = itr_reference.next()
                    val link = new ELinkWrapper(new ELink(e, reference, e.eGet(reference)))
                    links = link :: links
                }
            }
        }
    }

    override def allModelElements: List[EObjectWrapper] = elements

    override def allModelLinks: List[ELinkWrapper] = links

    override def toString: String = {
        val str_builder = new StringBuilder("")
        str_builder.append("elements (size="+ elements.size +"):\n")
        for (e : EObjectWrapper <- elements)
            str_builder.append("\t" + e.getEObject.toString + "\n")
        str_builder.append("\n")
        str_builder.append("links (size="+ links.size +"):\n")
        for (l : ELinkWrapper <- links) {
            str_builder.append("\t link \n")
            str_builder.append(l.getELink.toString(ntab = 2))
        }
        str_builder.toString()
    }

}
