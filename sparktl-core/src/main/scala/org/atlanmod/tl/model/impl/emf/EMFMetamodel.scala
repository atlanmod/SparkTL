package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.model.{Metamodel, Model}
import org.eclipse.emf.ecore.{EClass, EObject, EReference}

class EMFMetamodel (name: String) extends Metamodel[EObject, ELink, EClass, EReference] {

    override def toModelClass(sc: EClass, se: EObject): Option[EObject] = {
        if(se.eClass().equals(sc)) Some(se)
        else None
    }

    override def toModelReference(sr: EReference, sl: ELink): Option[ELink] = {
        if(sl.getType.equals(sr)) Some(sl)
        else None
    }

    override def equals(obj: Any): Boolean =
        obj match {
            case mm: EMFMetamodel => mm.name().equals(this.name())
            case _ => false
        }

    override def name(): String = this.name

    override def allModelElementsOfType(t: EClass, sm: Model[EObject, ELink, EClass]): List[EObject] =
        sm.asInstanceOf[EMFModel].allElementsOfType(t).toList
}




