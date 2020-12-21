package org.atlanmod.model.dynamic.classModel;

public class ClassClass extends ClassElement {

    public ClassClass(String id, String name){
        super(ClassModel.CLASS);
        super.eSet("id", id);
        super.eSet("name", name);
    }

    public String getId(){
        return (String) super.eGet("id");
    }

    public String getName(){
        return (String) super.eGet("name");
    }

    public void setId(String id){
        super.eSet("id", id);
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
