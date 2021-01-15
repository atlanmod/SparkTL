package org.atlanmod.model.dynamic.relationalModel;

public class RelationalTable extends RelationalElement{

    public RelationalTable(String id, String name){
        super(RelationalMetamodel.TABLE);
        super.eSetProperty("id", id);
        super.eSetProperty("name", name);
    }

    public String getId(){
        return (String) super.eGetProperty("id");
    }

    public void setId(String id){
        super.eSetProperty("id", id);
    }

    public String getName(){
        return (String) super.eGetProperty("name");
    }

    public void setName(String name){
        super.eSetProperty("name", name);
    }

    @Override
    public String toString() {
        return super.getType() +
                " (" +
                "id:" + this.getId() +
                ", name:" + this.getName() +
                ")";
    }

}
