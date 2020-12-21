package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.dynamic.DynamicElement;

public abstract class ClassElement extends DynamicElement {

    abstract String getId();

    public ClassElement(String classname) {
        super(classname);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassElement){
            ClassElement obj_ = (ClassElement) obj;
            return this.getType().equals(obj_.getType()) && this.getId().equals(obj_.getId());
        }
        return false;
    }

}
