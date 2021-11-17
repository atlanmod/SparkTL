package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.{EClass, EObject}

class EMFModel(model: EObject) extends Model[EObject, ELink, EClass]{

    override def allModelElements: Iterator[EObject] =
        collection.JavaConverters.asScalaIterator(model.eAllContents())

    override def allModelLinks: Iterator[ELink] = {
        throw new Exception("Not implemented yet")
    }

    override def allElementsOfType(cl: EClass) : Iterator[EObject] = {
        this.allModelElements.filter(obj => obj.eClass().equals(cl))
    }
}
