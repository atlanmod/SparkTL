package org.atlanmod.scratch.dynamic;

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

    public String getType() {
        return type;
    }

    public DynamicElement getSource() {
        return source;
    }

    public List<DynamicElement> getTarget() {
        return target;
    }
}
