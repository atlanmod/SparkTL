package org.atlanmod.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DynamicElement implements Serializable {

    private final String type;
    private final Map<String, Object> properties;
    private final Map<String, Object> references;

    public DynamicElement(String classname){
        this.type = classname;
        properties = new HashMap<>();
        references = new HashMap<>();
    }
    public DynamicElement(String namespace, String classname){
        this.type = namespace + ":" + classname;
        properties = new HashMap<>();
        references = new HashMap<>();
    }

    public DynamicElement(DynamicElement element){
        this.type = element.getType();
        this.properties = element.getProperties();
        this.references = element.getReferences();
    }

    public DynamicElement(DynamicElement element, Map<String, Object> properties, Map<String, Object> references){
        this.type = element.getType();
        this.properties = properties;
        this.references= references;
    }

    public  Map<String, Object> getProperties() {
        return this.properties;
    }

    public  Map<String, Object> getReferences() {
        return this.references;
    }

    public Object eGetProperty(String name){ return properties.get(name); }

    public void eSetProperty(String name, Object value){ properties.putIfAbsent(name, value); }

    public Object eGetReference(String name){ return references.get(name); }

    public void eSetReference(String name, Object value){ references.putIfAbsent(name, value); }

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
