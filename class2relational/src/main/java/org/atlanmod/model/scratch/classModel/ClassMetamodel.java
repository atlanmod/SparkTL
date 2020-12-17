package org.atlanmod.model.scratch.classModel;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;

public class ClassMetamodel implements Metamodel<ClassElement, ClassLink, String, String> {

    public String TAG_REF_CLASS = "class";
    public String TAG_REF_ATTRIBUTES = "attributes";
    public String TAG_CLASS_CLASS = "a_class";
    public String TAG_CLASS_ATTRIBUTE = "an_attribute";

    @Override
    public Option<ClassElement> toModelClass(String sc, ClassElement se) {
        // TODO
        return null;
    }

    @Override
    public Option<ClassLink> toModelReference(String sr, ClassLink sl) {
        // TODO
        return null;
    }
}