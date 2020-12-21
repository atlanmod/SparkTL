package org.atlanmod.model.dynamic.classModel;

import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

public class ClassToAttributes extends ClassLink {

    public ClassToAttributes(ClassClass source, List<ClassAttribute> target) {
        super(ClassMetamodel.CLASS_ATTRIBUTES, source, new ArrayList<>(target));
    }

    public ClassToAttributes(ClassClass source, scala.collection.immutable.List<ClassAttribute> target) {
        super(ClassMetamodel.CLASS_ATTRIBUTES, source, new ArrayList<>(JavaConverters.seqAsJavaList(target)));
    }

    public String toString() {
        StringBuilder target_ids = new StringBuilder("[");
        for (int i=0; i<getTarget().size(); i++)
            target_ids.append(((ClassElement) this.getTarget().get(i)).getId()).append(i == getTarget().size() - 1 ? "]" : ",");
        return "(" + ((ClassElement) this.getSource()).getId() + ", " + super.getType() + ", " + target_ids + ")";
    }

}
