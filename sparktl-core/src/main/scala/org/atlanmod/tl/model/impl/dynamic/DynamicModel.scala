package org.atlanmod.tl.model.impl.dynamic

//import org.atlanmod.EMFTool
import org.atlanmod.tl.model.Model
//import org.eclipse.emf.ecore.resource.Resource

class DynamicModel(elements: List[DynamicElement] = List(), links: List[DynamicLink] = List())
  extends Model[DynamicElement, DynamicLink]{

    override def allModelElements: List[DynamicElement] = elements

    override def allModelLinks: List[DynamicLink] = links

    def numberOfElements : Int = elements.size
    def numberOfLinks : Int = links.map(l => l.getTarget.size).sum

    override def toString: String = {
        var res = ""
        res += "elements (size=" + numberOfElements + "):\n------------------------\n"
        res += elements.mkString("\n")
        res += "\n\n"
        res += "links (size=" + numberOfLinks + "):\n------------------------\n"
        res += links.mkString("\n")
        res
    }
}

