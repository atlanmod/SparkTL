package org.atlanmod.class2relational.model.relationalmodel

class ColumnToType(source: RelationalColumn, target: RelationalTypable)
  extends RelationalLink(RelationalMetamodel.COLUMN_TYPE, source, List(target)){

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.getId + ")"

    override def getSource: RelationalColumn = source

    override def getTarget: List[RelationalTypable] =  List(target)

    def getTargetType : RelationalTypable = target

}

