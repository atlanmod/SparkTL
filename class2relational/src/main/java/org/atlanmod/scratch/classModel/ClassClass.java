package org.atlanmod.scratch.classModel;

import java.util.ArrayList;
import java.util.List;

public class ClassClass implements ClassElement {

    private String id;
    private String name;
    private List<ClassAttribute> attributes;

    public ClassClass(String id, String name){
        this.id = id;
        this.name = name;
        this.attributes = new ArrayList<ClassAttribute>();
    }

    public ClassClass(String id, String name, List<ClassAttribute> attributes){
        for(ClassAttribute attribute : attributes){
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

    public List<ClassAttribute> getAttributes(){
        return this.attributes;
    }

    public void addAttribute(ClassAttribute attribute){
        assert(attribute.getContainer() == null);
        attributes.add(attribute);
        attribute.setContainer(this);
    }
}
