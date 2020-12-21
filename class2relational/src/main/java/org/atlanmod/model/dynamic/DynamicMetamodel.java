package org.atlanmod.model.dynamic;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;
import scala.Some;

public class DynamicMetamodel<DE extends DynamicElement, DL extends DynamicLink> implements Metamodel<DE, DL, String, String> {

    @Override
    public Option<DE> toModelClass(String sc, DE se) {
        if (sc.equals(se.getType()))
            return new Some<>(se);
        else
            return scala.Option.apply(null);
    }

    @Override
    public Option<DL> toModelReference(String sr, DL sl) {
        if (sr.equals(sl.getType()))
            return new Some<>(sl);
        else
            return scala.Option.apply(null);
    }

    public boolean equals(DE e1, DE e2) {
        return e1.equals(e2);
    }
}