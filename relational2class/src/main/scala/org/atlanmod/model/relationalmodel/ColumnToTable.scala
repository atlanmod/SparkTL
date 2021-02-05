package org.atlanmod.model.relationalmodel

class ColumnToTable(source: RelationalColumn, target: RelationalTable)
  extends RelationalLink(RelationalMetamodel.COLUMN_TABLE, source, List(target)){

    override def toString: String =
        "(" + source.getId() + ", " + super.getType + ", " + target.getId() + ")"

    override def getSource: RelationalColumn = source

    override def getTarget: List[RelationalTable] =  List(target)

    def getTargetTable : RelationalTable = target

}

