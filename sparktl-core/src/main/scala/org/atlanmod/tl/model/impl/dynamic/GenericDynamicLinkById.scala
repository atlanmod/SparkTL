package org.atlanmod.tl.model.impl.dynamic

class GenericDynamicLinkById(type_ : String, source: Long, target: List[Long]) extends
DynamicLinkById(type_, source, target) {

    override def weak_equals(o: Any): Boolean =
        o match {
            case gdl: GenericDynamicLinkById =>
                gdl.type_.equals(type_) && gdl.source.equals(source) && gdl.target.equals(target)
            case _ => false
        }

}
