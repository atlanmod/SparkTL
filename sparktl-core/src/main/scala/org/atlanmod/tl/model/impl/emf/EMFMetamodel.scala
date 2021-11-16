package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.model.Metamodel
import org.eclipse.emf.ecore.{EClass, EObject, EReference}

class EMFMetamodel [ME <: EObject, ML <: ELink](name: String) extends Metamodel[ME, ML, EClass, EReference] {

    override def toModelClass(sc: EClass, se: ME): Option[ME] = {
        if(se.eClass().equals(sc)) Some(se)
        else None
    }

    override def toModelReference(sr: EReference, sl: ML): Option[ML] = {
        if(sl.getType.equals(sr)) Some(sl)
        else None
    }

    override def equals(obj: Any): Boolean =
        obj match {
            case mm: EMFMetamodel[ME, ML] => mm.name().equals(this.name())
            case _ => false
        }

    override def name(): String = this.name

}




