package org.atlanmod.model.scratch.relationalModel;

import org.atlanmod.tl.model.Model;
import scala.collection.immutable.List;

public class RelationalModel implements Model<RelationalElement, RelationalLink> {

    private RelationalMetamodel metamodel;
    private List<RelationalElement> elements;
    private List<RelationalLink> links;

    public RelationalModel(List<RelationalTable> tables) {
        // TODO
        this.metamodel = new RelationalMetamodel();
    }


    @Override
    public List<RelationalElement> allModelElements() {
        //TODO
        return null;
    }

    @Override
    public List<RelationalLink> allModelLinks() {
        //TODO
        return null;
    }
}
