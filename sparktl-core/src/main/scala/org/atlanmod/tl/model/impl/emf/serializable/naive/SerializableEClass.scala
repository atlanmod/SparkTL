package org.atlanmod.tl.model.impl.emf.serializable.naive

import org.eclipse.emf.ecore.EClass

class SerializableEClass (class_ : EClass) extends Serializable {
    def getEClass : EClass = class_
}
