package org.atlanmod.tl.model.impl.emf.serializable.string

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject

class SerializableEMFModel (model: SerializableEObject, converter: EMFStringConverter)
  extends Model[SerializableEObject, SerializableELink]{

    def this(model: EObject, converter: EMFStringConverter) =
        this(new SerializableEObject(model), converter)

    override def allModelElements: List[SerializableEObject] =
        collection.JavaConverters.asScalaIterator(model.eObject().eAllContents()).map(o => new SerializableEObject(o)).toList

    override def allModelLinks: List[SerializableELink] =
        throw new Exception("Not implemented yet")

}
