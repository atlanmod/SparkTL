package org.atlanmod.class2relational.transformation

import org.atlanmod.class2relational.model.relationalmodel.RelationalModel
import org.atlanmod.class2relational.model.relationalmodel.element.RelationalTable
import org.atlanmod.class2relational.model.relationalmodel.metamodel.RelationalMetamodel

object RelationalHelper {

    def isNotPivot_default(table: RelationalTable, model: RelationalModel, metamodel: RelationalMetamodel) : Boolean =
        !isPivot_default(table, model, metamodel)

    def isPivot_default(table: RelationalTable, model: RelationalModel, metamodel: RelationalMetamodel) : Boolean =
        table.getName.contains("_")

}
