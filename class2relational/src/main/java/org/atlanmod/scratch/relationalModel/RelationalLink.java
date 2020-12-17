package org.atlanmod.scratch.relationalModel;

import org.atlanmod.Link;

import java.util.List;

public class RelationalLink implements Link<RelationalElement, String, List<RelationalElement>> {

    private RelationalElement source;
    private String ref;
    private List<RelationalElement> target;

    public RelationalLink(RelationalElement source, String ref, List<RelationalElement> target){
        this.source = source;
        this.ref = ref;
        this.target = target;
    }

    @Override
    public RelationalElement getSource() {
        return this.source;
    }

    @Override
    public String getReference() {
        return this.ref;
    }

    @Override
    public List<RelationalElement> getTarget() {
        return this.target;
    }
}
