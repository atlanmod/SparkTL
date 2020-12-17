package org.atlanmod.scratch.relationalModel;

import java.util.ArrayList;
import java.util.List;

public class RelationalTable implements RelationalElement {

    private String id;
    private String name;
    private List<RelationalColumn> attributes;

    public RelationalTable(String id, String name){
        this.id = id;
        this.name = name;
        this.attributes = new ArrayList<RelationalColumn>();
    }

    public RelationalTable(String id, String name, List<RelationalColumn> attributes){
        for(RelationalColumn attribute : attributes){
            assert(attribute.getContainer() == null || attribute.getContainer() == this);
            attribute.setContainer(this);
        }
        this.id = id;
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.id;
    }

    public List<RelationalColumn> getAttributes(){
        return this.attributes;
    }

    public void addAttribute(RelationalColumn attribute){
        assert(attribute.getContainer() == null);
        attributes.add(attribute);
        attribute.setContainer(this);
    }
}
