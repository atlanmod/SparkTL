package org.atlanmod.scratch.dynamic;

import java.util.HashMap;
import java.util.Map;

public class DynamicElement {

    private String type;
    // private String namespace;
    private Map<String, Object> properties;

    public DynamicElement(String type){
        this.type = type;
        properties = new HashMap();
    }

    public DynamicElement(DynamicElement element){
        this.type = element.getType();
        this.properties = element.getProperties();
    }

    public DynamicElement(DynamicElement element, Map<String, Object> properties){
        this.type = element.getType();
        this.properties = properties;
    }

    public  Map<String, Object> getProperties() {
        return this.properties;
    }

    public Object eGet(String name){
        return properties.get(name);
    }

    public String getType(){
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DynamicElement){
            DynamicElement obj_ = (DynamicElement) obj;
            return this.getType() == obj_.getType() && this.getProperties() == obj_.getProperties();
        }
        return false;
    }

}
