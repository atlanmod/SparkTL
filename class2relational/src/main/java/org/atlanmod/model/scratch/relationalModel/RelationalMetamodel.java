package org.atlanmod.model.scratch.relationalModel;

import org.atlanmod.tl.model.Metamodel;
import scala.Option;

public class RelationalMetamodel implements Metamodel<RelationalElement, RelationalLink, String, String> {

    public String TAG_REF_TABLE = "table";
    public String TAG_REF_COLUMNS = "columns";
    public String TAG_CLASS_TABLE = "a_table";
    public String TAG_CLASS_COLUMN = "a_column";

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