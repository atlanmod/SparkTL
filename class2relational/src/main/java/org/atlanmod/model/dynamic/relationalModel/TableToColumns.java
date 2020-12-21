package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.dynamic.DynamicElement;
import org.atlanmod.model.dynamic.classModel.ClassAttribute;
import org.atlanmod.model.dynamic.classModel.ClassClass;
import org.atlanmod.model.dynamic.classModel.ClassModel;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.List;

public class TableToColumns extends RelationalLink {

    public TableToColumns(RelationalTable source, List<RelationalColumn> target){
        super(RelationalModel.TABLE_COLUMNS, source, new ArrayList<>(target));
    }

    public TableToColumns(RelationalTable source, scala.collection.immutable.List<RelationalColumn> target) {
        super(RelationalModel.TABLE_COLUMNS, source, new ArrayList<>(JavaConverters.seqAsJavaList(target)));
    }

    public String toString() {
        StringBuilder target_ids = new StringBuilder("[");
        for (int i=0; i<getTarget().size(); i++)
            target_ids.append(((RelationalElement) this.getTarget().get(i)).getId()).append(i == getTarget().size() - 1 ? "]" : ",");
        return "(" + ((RelationalElement) this.getSource()).getId() + ", " + super.getType() + ", " + target_ids + ")";
    }

}
