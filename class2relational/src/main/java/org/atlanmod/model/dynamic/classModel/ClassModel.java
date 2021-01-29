package org.atlanmod.model.dynamic.classModel;

import org.atlanmod.model.DynamicElement;
import org.atlanmod.model.DynamicLink;
import org.atlanmod.model.DynamicModel;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

public class ClassModel extends DynamicModel {

    public ClassModel(){
        super();
    }

    public ClassModel(List<ClassElement> elements, List<ClassLink> links){
        super();
        this.elements.addAll(elements);
        this.links.addAll(links);
    }

    public ClassModel(scala.collection.immutable.List<ClassElement> elements,
                      scala.collection.immutable.List<ClassLink> links){
        super();
        this.elements.addAll(JavaConverters.seqAsJavaList(elements));
        this.links.addAll(JavaConverters.seqAsJavaList(links));
    }

    public scala.collection.immutable.List<ClassElement> allClassElements() {
        List<ClassElement> result = new ArrayList<>();
        for(DynamicElement e: this.elements)
            result.add((ClassElement) e);
        return JavaConverters.asScalaBuffer(result).toList();
    }

    public scala.collection.immutable.List<ClassLink> allClassLinks() {
        List<ClassLink> result = new ArrayList<>();
        for(DynamicLink e: this.links)
            result.add((ClassLink) e);
        return JavaConverters.asScalaBuffer(result).toList();
    }


}
