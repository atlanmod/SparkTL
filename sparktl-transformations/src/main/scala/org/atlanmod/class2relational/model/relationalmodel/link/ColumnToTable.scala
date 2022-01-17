package org.atlanmod.class2relational.model.relationalmodel.link

import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable}
import org.atlanmod.class2relational.model.relationalmodel.RelationalLink
import org.atlanmod.class2relational.model.relationalmodel.metamodel.RelationalMetamodelNaive

class ColumnToTable(source: RelationalColumn, target: RelationalTable)
  extends RelationalLink(RelationalMetamodelNaive.COLUMN_TABLE, source, List(target)){

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.getId + ")"

    override def getSource: RelationalColumn = source

    override def getTarget: List[RelationalTable] =  List(target)

    def getTargetTable : RelationalTable = target

}

