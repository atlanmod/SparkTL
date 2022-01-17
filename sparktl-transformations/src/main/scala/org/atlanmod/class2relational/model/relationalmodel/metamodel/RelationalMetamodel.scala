package org.atlanmod.class2relational.model.relationalmodel.metamodel

import org.atlanmod.class2relational.model.relationalmodel.RelationalModel
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalClassifier, RelationalType}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

trait RelationalMetamodel {

    final val TABLE = "Table"
    final val COLUMN = "Column"
    final val TYPE = "Type"
    final val TABLE_COLUMNS = "columns"
    final val TABLE_KEYS = "keys"
    final val COLUMN_TABLE = "table"
    final val COLUMN_TYPE = "type"

    def metamodel: DynamicMetamodel[DynamicElement, DynamicLink] = new DynamicMetamodel[DynamicElement, DynamicLink]("RelationalMetamodel")

    def getTableColumns(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]]
    def getTableKeys(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]]
    def getKeyOf(c: RelationalColumn, model: RelationalModel): Option[RelationalTable]
    def getColumnType(column: RelationalColumn, model: RelationalModel): Option[RelationalClassifier]

    def getAllTable(model: RelationalModel): List[RelationalTable]
    def getAllColumns(model: RelationalModel): List[RelationalColumn]
    def getAllType(model: RelationalModel): List[RelationalType]
    def getAllTypable(model: RelationalModel): List[RelationalClassifier]

}
