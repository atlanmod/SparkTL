package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject

class EMFModel(model: EObject) extends Model[EObject, ELink]{

    override def allModelElements: Iterator[EObject] =
        collection.JavaConverters.asScalaIterator(model.eAllContents())

    override def allModelLinks: Iterator[ELink] = {
        throw new Exception("Not implemented yet")
    }

}
