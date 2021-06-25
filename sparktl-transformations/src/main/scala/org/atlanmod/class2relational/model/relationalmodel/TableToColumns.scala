package org.atlanmod.class2relational.model.relationalmodel

class TableToColumns (source: RelationalTable, target: List[RelationalColumn])
  extends RelationalLink(RelationalMetamodel.TABLE_COLUMNS, source, target){

    def this(source: RelationalTable, target: RelationalColumn) =
        this(source, List(target))

    override def getSource: RelationalTable = source
    override def getTarget: List[RelationalColumn] = target

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.map(a => a.getId).mkString("[", ", ", "]") + ")"

}
