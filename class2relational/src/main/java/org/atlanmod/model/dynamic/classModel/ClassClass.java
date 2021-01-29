package org.atlanmod.model.dynamic.classModel;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

public class ClassClass extends ClassElement {

    public ClassClass(String id, String name){
        super(ClassMetamodel.CLASS);
        super.eSetProperty("id", id);
        super.eSetProperty("name", name);
    }

    public String getId(){ return (String) super.eGetProperty("id"); }

    public String getName(){ return (String) super.eGetProperty("name"); }

    @SuppressWarnings("unchecked")
    public List<ClassAttribute> getAttributes(){
        return ((List<ClassAttribute>) super.eGetReference("attributes"));
    }

    @SuppressWarnings("unchecked")
    public void addAttributes(List<ClassAttribute> attributes){
        super.eSetReference("attributes", new ArrayList<ClassAttribute>());
        ((List<ClassAttribute>) super.eGetReference("attributes")).addAll(attributes) ;
    }

    @SuppressWarnings("unchecked")
    public void addAttributes(scala.collection.immutable.List<ClassAttribute> attributes){
        super.eSetReference("attributes", new ArrayList<ClassAttribute>());
        ((List<ClassAttribute>) super.eGetReference("attributes")).addAll(JavaConverters.seqAsJavaList(attributes)) ;
    }

    @SuppressWarnings("unchecked")
    public void addAttribute(ClassAttribute attribute){
        super.eSetReference("attributes", new ArrayList<ClassAttribute>());
        ((List<ClassAttribute>) super.eGetReference("attributes")).add(attribute) ;
    }

    public void setId(String id){
        super.eSetProperty("id", id);
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
