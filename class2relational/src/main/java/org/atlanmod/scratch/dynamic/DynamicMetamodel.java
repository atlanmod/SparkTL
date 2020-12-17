package org.atlanmod.scratch.dynamic;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;
import scala.Some;

public class DynamicMetamodel implements Metamodel<DynamicElement, DynamicLink, String, String> {

    @Override
    public Option<DynamicElement> toModelClass(String sc, DynamicElement se) {
        if (sc.equals(se.getType()))
            return new Some<>(se);
        else
            return scala.Option.apply(null);
    }

    @Override
    public Option<DynamicLink> toModelReference(String sr, DynamicLink sl) {
        if (sr.equals(sl.getType()))
            return new Some<>(sl);
        else
            return scala.Option.apply(null);
    }

    public boolean equals(DynamicElement e1, DynamicElement e2) {
        return e1.equals(e2);
    }
}