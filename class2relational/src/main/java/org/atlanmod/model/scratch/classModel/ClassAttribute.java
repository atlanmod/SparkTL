package org.atlanmod.model.scratch.classModel;

public class ClassAttribute implements ClassElement {

    private String id;
    private String name;
    private ClassClass container;

    public ClassAttribute(String id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.id;
    }

    public ClassClass getContainer() {
        return this.container;
    }

    protected void setContainer(ClassClass class_) {
        this.container = class_;
    }
}
