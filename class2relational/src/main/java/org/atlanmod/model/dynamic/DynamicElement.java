package org.atlanmod.model.dynamic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DynamicElement implements Serializable {

    private final String type;
    private final Map<String, Object> properties;

    public DynamicElement(String classname){
        this.type = classname;
        properties = new HashMap<>();
    }
    public DynamicElement(String namespace, String classname){
        this.type = namespace + ":" + classname;
        properties = new HashMap<>();
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

    public void eSet(String name, Object value){ properties.putIfAbsent(name, value); }

    public String getType(){
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DynamicElement){
            DynamicElement obj_ = (DynamicElement) obj;
            return this.getType().equals(obj_.getType()) && this.getProperties().equals(obj_.getProperties());
        }
        return false;
    }

    public String toString() {
        StringBuilder str_builder = new StringBuilder();
        str_builder.append(type);
        if (properties.keySet().isEmpty()) return str_builder.toString();
        str_builder.append("\n");
        for(String property : properties.keySet()){
            str_builder.append(property).append("\n");
        }
        return str_builder.toString();
    }

}
