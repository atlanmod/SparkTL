package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.dynamic.DynamicElement;

import java.util.Map;

public abstract class ClassElement extends DynamicElement {

    abstract String getId();

    public ClassElement(String classname) {
        super(classname);
    }
}
