package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.dynamic.DynamicElement;

import java.util.ArrayList;
import java.util.List;

public class ClassToAttributes extends ClassLink {

    public ClassToAttributes(ClassClass source, List<ClassAttribute> target) {
        super(ClassModel.CLASS_ATTRIBUTES, source, null);
        this.setTarget(new ArrayList<>(target));
    }

    public String toString() {
        StringBuilder target_ids = new StringBuilder("[");
        for (int i=0; i<getTarget().size(); i++)
            target_ids.append(((ClassElement) this.getTarget().get(i)).getId()).append(i == getTarget().size() - 1 ? "]" : ",");
        return "(" + ((ClassElement) this.getSource()).getId() + ", " + super.getType() + ", " + target_ids + ")";
    }

}
