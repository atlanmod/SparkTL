package org.atlanmod.scratch.relationalModel;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;

public class RelationalMetamodel implements Metamodel<RelationalElement, RelationalLink, String, String> {

    @Override
    public Option<RelationalElement> toModelClass(String sc, RelationalElement se) {
        // TODO
        return null;
    }

    @Override
    public Option<RelationalLink> toModelReference(String sr, RelationalLink sl) {
        // TODO
        return null;
    }
}