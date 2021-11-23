package org.atlanmod.tl.model.impl.emf

import org.atlanmod.tl.model.Model
import org.eclipse.emf.ecore.EObject

class EMFModel(model: EObject) extends Model[EObject, ELink]{

    override def allModelElements: List[EObject] =
        collection.JavaConverters.asScalaIterator(model.eAllContents()).toList

    override def allModelLinks: List[ELink] = {
        throw new Exception("Not implemented yet")
    }
}
