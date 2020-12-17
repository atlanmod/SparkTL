package org.atlanmod.scratch.classModel;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;

public class ClassMetamodel implements Metamodel<ClassElement, ClassLink, String, String> {

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