package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.DynamicElement;
import org.atlanmod.model.DynamicLink;

import java.util.List;

public abstract class RelationalLink extends DynamicLink {

    public RelationalLink(String type, DynamicElement source, List<DynamicElement> target) {
        super(type, source, target);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationalLink){
            RelationalLink obj_ = (RelationalLink) obj;
            return  this.getType().equals(obj_.getType())
                    && this.getSource().equals(obj_.getSource())
                    && this.getTarget().equals(obj_.getTarget());
        }
        return false;
    }

}
