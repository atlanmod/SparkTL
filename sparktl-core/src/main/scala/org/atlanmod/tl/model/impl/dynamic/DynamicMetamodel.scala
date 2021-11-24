package org.atlanmod.tl.model.impl.dynamic

import org.atlanmod.tl.model.Metamodel

class DynamicMetamodel[DE <: DynamicElement, DL <: DynamicLink](name: String) extends Metamodel[DE, DL, String, String] {

    override def toModelClass(sc: String, se: DE): Option[DE] = {
        if(se.getType.equals(sc)) Some(se)
        else None
    }

    override def toModelReference(sr: String, sl: DL): Option[DL] = {
        if (sl.getType.equals(sr)) Some(sl)
        else None
    }

    override def name(): String = this.name

    override def equals(obj: Any): Boolean =
        obj match {
            case mm: DynamicMetamodel[DE, DL] => mm.name().equals(this.name())
            case _ => false
        }

    def allModelElementsOfType(t: String, sm: DynamicModel): List[DynamicElement] =
        sm.allModelElements.filter(e => e.getType.equals(t))

    def allModelLinksOfType(t: String, sm: DynamicModel): List[DynamicLink] =
        sm.allModelLinks.filter(l => l.getType.equals(t))

    def allLinksOfTypeOfElement(elem: DynamicElement, ref: String, sm: DynamicModel): Option[List[DynamicElement]] = {
        sm.getLinks(elem) match {
            case Some(links) =>
                links.find(link => ref.equals(link.getType)) match {
                    case Some(link) => Some(link.getTarget)
                    case _ => None
                }
            case _ => None
        }
    }
}
