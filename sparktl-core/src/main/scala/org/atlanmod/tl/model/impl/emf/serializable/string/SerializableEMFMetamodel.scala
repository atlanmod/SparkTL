package org.atlanmod.tl.model.impl.emf.serializable.string

import org.atlanmod.tl.model.{Metamodel, Model}

class SerializableEMFMetamodel (name: String, converter: EMFStringConverter) extends Metamodel[SerializableEObject, SerializableELink, String, String] {

    override def toModelClass(sc: String, se: SerializableEObject): Option[SerializableEObject] =
        if(se.eClass().equals(converter.EClassFromString(sc))) Some(se)
        else None

    override def toModelReference(sr: String, sl: SerializableELink): Option[SerializableELink] =
        if(sl.getType.equals(converter.EReferenceFromString(sr))) Some(sl)
        else None

    def allModelElementsOfType(t: String, sm: Model[SerializableEObject, SerializableELink]):
    List[SerializableEObject] =
        sm.allModelElements.filter(eobject => t.equals(converter.EClassToString(eobject.eClass())))

    override def equals(obj: Any): Boolean =
        obj match {
            case mm: SerializableEMFMetamodel => mm.name().equals(this.name())
            case _ => false
        }

    override def name(): String = name

}
