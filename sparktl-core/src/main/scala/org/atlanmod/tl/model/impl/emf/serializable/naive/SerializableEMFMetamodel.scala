package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.atlanmod.tl.model.{Metamodel, Model}
import org.eclipse.emf.ecore.{EClass, EReference}

class SerializableEMFMetamodel (name: String) extends Metamodel[SerializableEObject, SerializableELink, SerializableEClass, SerializableEReference] {

    override def toModelClass(sc: SerializableEClass, se: SerializableEObject): Option[SerializableEObject] = {
        if(se.eClass().equals(sc)) Some(se)
        else None
    }

    def toModelClass(sc: EClass, se: SerializableEObject): Option[SerializableEObject] = {
        if(se.eClass().equals(sc)) Some(se)
        else None
    }

    override def toModelReference(sr: SerializableEReference, sl: SerializableELink): Option[SerializableELink] = {
        if(sl.getType.equals(sr)) Some(sl)
        else None
    }


    def toModelReference(sr: EReference, sl: SerializableELink): Option[SerializableELink] = {
        if(sl.getType.equals(sr)) Some(sl)
        else None
    }

    override def equals(obj: Any): Boolean =
        obj match {
            case mm: SerializableEMFMetamodel => mm.name().equals(this.name())
            case _ => false
        }

    override def name(): String = this.name

    def allModelElementsOfType(t: SerializableEClass, sm: Model[SerializableEObject, SerializableELink])
    : List[SerializableEObject] =
        sm match {
            case model: SerializableEMFModel => model.allModelElements.filter(e => toModelClass(t, e).isDefined)
            case _ => throw new Exception("allModelElementsOfType from EMFMetamodel is only defined for EMFModel instances.")
        }

}




