package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.DynamicElement;

public abstract class RelationalElement extends DynamicElement {

    abstract String getId();

    public RelationalElement(String classname) {
        super(classname);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationalElement){
            RelationalElement obj_ = (RelationalElement) obj;
            return this.getType().equals(obj_.getType()) && this.getId().equals(obj_.getId());
        }
        return false;
    }

}
