package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.dynamic.DynamicElement;
import org.atlanmod.model.dynamic.DynamicLink;

import java.util.List;

public abstract class RelationalLink extends DynamicLink {

    public RelationalLink(String type, DynamicElement source, List<DynamicElement> target) {
        super(type, source, target);
    }

}
