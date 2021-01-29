package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.DynamicElement;

import java.util.ArrayList;
import java.util.List;

public class AttributeToClass extends ClassLink {

    public AttributeToClass(ClassAttribute source, ClassClass target) {
        super(ClassMetamodel.ATTRIBUTE_CLASS, source, null);
        List<DynamicElement> new_target = new ArrayList<>();
        new_target.add(target);
        this.setTarget(new_target);
    }

    public String toString() {
        return "(" +
                ((ClassElement) this.getSource()).getId() +
                ", " + super.getType() +
                ", " + ((ClassElement) this.getTarget().get(0)).getId() +
                ")";
    }
}
