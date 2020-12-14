package org.atlanmod.parallel

import org.atlanmod.tl.model.Metamodel
import _root_.org.atlanmod.wrapper.{EClassWrapper, ELinkWrapper, EObjectWrapper, EReferenceWrapper}

class EMFMetamodelSerializable extends Metamodel[EObjectWrapper, ELinkWrapper, EClassWrapper, EReferenceWrapper]{

    override def toModelClass(sc: EClassWrapper, se: EObjectWrapper): Option[EObjectWrapper] = {
        val cl = sc.unwrap
        val ob = se.unwrap
        if (ob.eClass.getEAllSuperTypes.contains(cl) | ob.eClass() == cl) Some(se) else None
    }

    override def toModelReference(sr: EReferenceWrapper, sl: ELinkWrapper): Option[ELinkWrapper] = {
        val re = sr.unwrap
        val li = sl.unwrap
        if (li.getReference.equals(re)) Some(sl) else None
    }

    override def equals(that: Any): Boolean = {
        that match {
            case _ : EMFMetamodelSerializable => true
            case _ => false
        }
    }

}