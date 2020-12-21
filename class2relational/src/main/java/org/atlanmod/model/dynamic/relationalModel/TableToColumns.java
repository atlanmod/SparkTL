package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.dynamic.DynamicElement;

import java.util.ArrayList;
import java.util.List;

public class TableToColumns extends RelationalLink {

    public TableToColumns(RelationalTable source, List<RelationalColumn> target){
        super(RelationalModel.TABLE_COLUMNS, source, null);
        this.setTarget(new ArrayList<>(target));
    }

    public String toString() {
        StringBuilder target_ids = new StringBuilder("[");
        for (int i=0; i<getTarget().size(); i++)
            target_ids.append(((RelationalElement) this.getTarget().get(i)).getId()).append(i == getTarget().size() - 1 ? "]" : ",");
        return "(" + ((RelationalElement) this.getSource()).getId() + ", " + super.getType() + ", " + target_ids + ")";
    }

}
