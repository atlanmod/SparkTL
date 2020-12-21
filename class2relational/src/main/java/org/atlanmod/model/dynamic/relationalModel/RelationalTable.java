package org.atlanmod.model.dynamic.relationalModel;

public class RelationalTable extends RelationalElement{

    public RelationalTable(String id, String name){
        super(RelationalModel.TABLE);
        super.eSet("id", id);
        super.eSet("name", name);
    }

    public String getId(){
        return (String) super.eGet("id");
    }

    public void setId(String id){
        super.eSet("id", id);
    }

    public String getName(){
        return (String) super.eGet("name");
    }

    public void setName(String name){
        super.eSet("name", name);
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
