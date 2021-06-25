package org.atlanmod.tl.model.impl.dynamic

import org.atlanmod.tl.model.Metamodel

class DynamicMetamodel[DE <: DynamicElement, DL <: DynamicLink] extends Metamodel[DE, DL, String, String] {

    override def toModelClass(sc: String, se: DE): Option[DE] = {
        if(se.getType.equals(sc)) Some(se)
        else None
    }

    override def toModelReference(sr: String, sl: DL): Option[DL] = {
        if (sl.getType.equals(sr)) Some(sl)
        else None
    }

    override def equals(obj: Any): Boolean =
        obj match {
            case _: DynamicMetamodel[DE, DL] => true
            case _ => false
        }
}
