package org.atlanmod.model.dynamic;

import scala.Serializable;
import scala.collection.JavaConverters;
import java.util.List;

public class DynamicLink implements Serializable {

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

    protected void setSource(DynamicElement source) { this.source = source; }

    public List<DynamicElement> getTarget() {
        return target;
    }

    protected void setTarget(List<DynamicElement> target) { this.target = target; }

    public String toString() {
        return "(" + source.toString() + ", " + type + ", " + target.toString() + ")";
    }

    public String toString(int ntab) {
        return source.toString() + "\n" + type + "\n" + target.toString() + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DynamicLink){
            DynamicLink obj_ = (DynamicLink) obj;
            return this.getType().equals(obj_.getType())
                    && this.getSource().equals(obj_.getSource())
                    && this.getTarget().equals(obj_.getTarget());
        }
        return false;
    }
}
