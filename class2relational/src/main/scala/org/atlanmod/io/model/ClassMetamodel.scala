package org.atlanmod.io.model

import org.atlanmod.io.model.classM.{ClassClass, ClassLink, ClassElement, ClassReference}
import org.atlanmod.tl.model.Metamodel

class ClassMetamodel extends Metamodel[ClassElement, ClassLink, ClassClass, ClassReference] {

    override def toModelClass(sc: ClassClass, se: ClassElement): Option[ClassClass] = ???

    override def toModelReference(sr: ClassReference, sl: ClassLink): Option[ClassReference] = ???

}
