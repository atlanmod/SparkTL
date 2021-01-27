package org.atlanmod.model.dynamic.classModel;

public class ClassAttribute extends ClassElement {

    public ClassAttribute(String id, String name){
        super(ClassMetamodel.ATTRIBUTE);
        super.eSetProperty("id", id);
        super.eSetProperty("name", name);
        super.eSetProperty("derived", false);
        super.eSetProperty("multivalued", false);
    }

    public ClassAttribute(String id, String name, Boolean derived, Boolean multivalued){
        super(ClassMetamodel.ATTRIBUTE);
        super.eSetProperty("id", id);
        super.eSetProperty("name", name);
        super.eSetProperty("derived", derived);
        super.eSetProperty("multivalued", multivalued);
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

    public Boolean isDerived(){
        return (Boolean) super.eGetProperty("derived");
    }

    public void setDerived(Boolean derived){
        super.eSetProperty("derived", derived);
    }

    public Boolean isMultivalued(){
        return (Boolean) super.eGetProperty("multivalued");
    }

    public void setMultivalued(Boolean multivalued){
        super.eSetProperty("multivalued", multivalued);
    }

    public ClassClass getClass_(){
        return (ClassClass) super.eGetReference("class");
    }

    public void setClass_(ClassClass c){
        super.eSetReference("class", c);
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
