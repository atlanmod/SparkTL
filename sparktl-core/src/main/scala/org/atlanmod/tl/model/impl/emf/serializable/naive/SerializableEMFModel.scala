package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject

class SerializableEMFModel(model: SerializableEObject) extends Model[SerializableEObject, SerializableELink]{

    def this(model: EObject) = {
        this(new SerializableEObject(model))
    }

    override def allModelElements: List[SerializableEObject] =
        collection.JavaConverters.asScalaIterator(model.eObject().eAllContents()).map(o => new SerializableEObject(o)).toList

    override def allModelLinks: List[SerializableELink] = {
        throw new Exception("Not implemented yet") // TODO
    }
}
