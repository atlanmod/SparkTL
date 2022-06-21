package org.atlanmod.tl.model.impl.dynamic

class GenericDynamicLink(type_ : String, source: GenericDynamicElement, target: List[GenericDynamicElement]) extends
DynamicLink(type_, source, target) {

    override def weak_equals(o: Any): Boolean =
        o match {
            case gdl: GenericDynamicLink =>
                gdl.type_.equals(type_) && gdl.source.equals(source) && gdl.target.equals(target)
            case _ => false
        }

}
