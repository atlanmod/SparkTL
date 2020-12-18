package org.atlanmod.model.dynamic;

import scala.collection.JavaConverters;
import java.util.List;

public class DynamicLink {

    private String type;
    private DynamicElement source;
    private List<DynamicElement> target;

    public DynamicLink(String type, DynamicElement source, List<DynamicElement> target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public DynamicLink(String type, DynamicElement source, scala.collection.immutable.List<DynamicElement> target) {
        this.type = type;
        this.source = source;
        this.target =  JavaConverters.seqAsJavaList(target);
    }

    public String getType() {
        return type;
    }

    public DynamicElement getSource() {
        return source;
    }

    public List<DynamicElement> getTarget() {
        return target;
    }

    public String toString() {
        return "(" + source.toString() + ", " + type + ", " + target.toString() + ")";
    }

    public String toString(int ntab) {
        return source.toString() + "\n" + type + "\n" + target.toString() + "\n";
    }
}
