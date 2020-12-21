package org.atlanmod.model.dynamic.classModel;

public class ClassAttribute extends ClassElement {

    public ClassAttribute(String id, String name){
        super(ClassModel.ATTRIBUTE);
        super.eSet("id", id);
        super.eSet("name", name);
        super.eSet("derived", false);
    }

    public ClassAttribute(String id, String name, Boolean derived){
        super(ClassModel.ATTRIBUTE);
        super.eSet("id", id);
        super.eSet("name", name);
        super.eSet("derived", derived);
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

    public Boolean isDerived(){
        return (Boolean) super.eGet("derived");
    }

    public void setDerived(Boolean derived){
        super.eSet("derived", derived);
    }

    @Override
    public String toString() {
        return super.getType() +
                "(" +
                "id:" + this.getId() +
                ", name:" + this.getName() +
                ", derived:" + this.isDerived() +
                ")";
    }
}
