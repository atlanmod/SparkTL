package org.atlanmod.model.classmodel

import org.atlanmod.model.DynamicLink

abstract class ClassLink(type_ : String, source: ClassElement, target: List[ClassElement])
  extends DynamicLink(type_, source, target){

    override def equals(o: Any): Boolean = {
        o match {
            case obj: ClassLink => obj.getType.equals(type_) & obj.getSource.equals(source) & obj.getTarget.equals(target)
            case _ => false
        }
    }

}
