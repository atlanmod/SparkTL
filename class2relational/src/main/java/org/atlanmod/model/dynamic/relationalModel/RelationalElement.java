package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.dynamic.DynamicElement;

public abstract class RelationalElement extends DynamicElement {

    abstract String getId();

    public RelationalElement(String classname) {
        super(classname);
    }

}
