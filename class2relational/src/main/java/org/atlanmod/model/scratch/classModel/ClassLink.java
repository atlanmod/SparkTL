package org.atlanmod.model.scratch.classModel;

import org.atlanmod.Link;

import java.util.List;

public class ClassLink implements Link<ClassElement, String, List<ClassElement>> {

    private ClassElement source;
    private String ref;
    private List<ClassElement> target;

    public ClassLink(ClassElement source, String ref, List<ClassElement> target){
        this.source = source;
        this.ref = ref;
        this.target = target;
    }

    @Override
    public ClassElement getSource() {
        return this.source;
    }

    @Override
    public String getReference() {
        return this.ref;
    }

    @Override
    public List<ClassElement> getTarget() {
        return this.target;
    }
}
