package org.atlanmod.model.classmodel

import org.atlanmod.model.DynamicLink
import org.atlanmod.tl.util.ListUtils

abstract class ClassLink(type_ : String, source: ClassElement, target: List[ClassElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ClassLink => obj.getType.equals(type_) & obj.getSource.equals(source) & obj.getTarget.equals(target)
            case _ => false
        }
    }

    override def weak_equals(o: Any): Boolean = {
        o match {
            case obj: ClassLink =>
                obj.getSource.weak_equals(source) &&
                  obj.getType.equals(type_) &&
                  ListUtils.weak_eqList(obj.getTarget, target)
            case _ => false
        }
    }



}
