package org.atlanmod.model.dynamic.relationalModel;

import org.atlanmod.model.dynamic.DynamicElement;

import java.util.ArrayList;
import java.util.List;

public class ColumnToTable extends RelationalLink {

    public ColumnToTable(RelationalColumn source, RelationalTable target) {
        super(RelationalModel.COLUMN_TABLE, source, null);
        List<DynamicElement> new_target = new ArrayList<>();
        new_target.add(target);
        this.setTarget(new_target);
    }

    public String toString() {
        return "(" +
                ((RelationalElement) this.getSource()).getId() +
                ", " + super.getType() +
                ", " + ((RelationalElement) this.getTarget().get(0)).getId() +
                ")";
    }
}
