package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.dynamic.DynamicElement;
import org.atlanmod.model.dynamic.DynamicLink;

import java.util.List;

public abstract class ClassLink extends DynamicLink {

    public ClassLink(String type, DynamicElement source, List<DynamicElement> target) {
        super(type, source, target);
    }
}
