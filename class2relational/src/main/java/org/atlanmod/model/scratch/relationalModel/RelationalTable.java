package org.atlanmod.model.scratch.relationalModel;

import java.util.ArrayList;
import java.util.List;

public class RelationalTable implements RelationalElement {

    private String id;
    private String name;
    private List<RelationalColumn> columns;

    public RelationalTable(String id, String name){
        this.id = id;
        this.name = name;
        this.columns = new ArrayList<>();
    }

    public RelationalTable(String id, String name, List<RelationalColumn> columns){
        for(RelationalColumn attribute : columns){
            assert(attribute.getContainer() == null || attribute.getContainer() == this);
            attribute.setContainer(this);
        }
        this.id = id;
        this.name = name;
        this.columns = columns;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.id;
    }

    public List<RelationalColumn> getColumns(){
        return this.columns;
    }

    public void addAttribute(RelationalColumn column){
        assert(column.getContainer() == null || column.getContainer() == this);
        columns.add(column);
        column.setContainer(this);
    }
}
