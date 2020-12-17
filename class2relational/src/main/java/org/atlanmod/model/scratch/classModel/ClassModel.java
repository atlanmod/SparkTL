package org.atlanmod.model.scratch.classModel;

import org.atlanmod.tl.model.Model;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

public class ClassModel implements Model<ClassElement, ClassLink> {

    private ClassMetamodel metamodel;
    private List<ClassElement> elements;
    private List<ClassLink> links;

    public ClassModel(List<ClassClass> classes) {
        elements = new ArrayList<>();
        links = new ArrayList<>();

        for(ClassClass class_ : classes) {
            elements.add(class_);
            if (!class_.getAttributes().isEmpty()){
                List<ClassElement> attributes = new ArrayList<>();
                for(ClassAttribute attribute : class_.getAttributes())
                    attributes.add((ClassElement) attribute);
                links.add(new ClassLink(class_, metamodel.TAG_REF_ATTRIBUTES, attributes));
                for(ClassAttribute attribute: class_.getAttributes()){
                    List<ClassElement> dest = new ArrayList<>();
                    dest.add(class_);
                    links.add(new ClassLink((ClassElement) attribute, metamodel.TAG_REF_CLASS, dest));
                }
            }
        }

        this.metamodel = new ClassMetamodel();
    }


    @Override
    public scala.collection.immutable.List<ClassElement> allModelElements() {
        return JavaConverters.asScalaBuffer(elements).toList();
    }

    @Override
    public scala.collection.immutable.List<ClassLink> allModelLinks() {
        return JavaConverters.asScalaBuffer(links).toList();
    }
}
