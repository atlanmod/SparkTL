package org.atlanmod.model.scratch.relationalModel;

public class RelationalColumn implements RelationalElement {

    private String id;
    private String name;
    private RelationalTable container;

    public RelationalColumn(String id, String name){
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

    public RelationalTable getContainer() {
        return this.container;
    }

    protected void setContainer(RelationalTable class_) {
        this.container = class_;
    }
}
