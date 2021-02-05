package org.atlanmod.model.relationalmodel

import scala.annotation.tailrec

object RelationalMetamodel {

    final val TABLE = "Table"
    final val COLUMN = "Column"
    final val TYPE = "Type"
    final val TABLE_COLUMNS = "columns"
    final val TABLE_KEY = "key"
    final val COLUMN_TABLE = "table"

    @tailrec
    private def getColumnReferenceOnLinks(c: RelationalColumn, l: List[RelationalLink]): Option[RelationalTable] =
        l match {
            case (h: ColumnToTable) :: l2 =>
                if (h.getSource.equals(c)) Some(h.getTargetTable)
                else getColumnReferenceOnLinks(c, l2)
            case _ :: l2 => getColumnReferenceOnLinks(c, l2)
            case List() => None
        }

    def getColumnReference(c: RelationalColumn, model: RelationalModel): Option[RelationalTable] =
        getColumnReferenceOnLinks(c, model.allModelLinks)

    @tailrec
    private def getTableColumnsOnLinks(t: RelationalTable, l: List[RelationalLink]): Option[List[RelationalColumn]] =
      l match {
          case (h: TableToColumns) :: l2 =>
              if (h.getSource.equals(t)) Some(h.getTarget)
              else getTableColumnsOnLinks(t, l2)
          case _ :: l2 => getTableColumnsOnLinks(t, l2)
          case List() => None
      }

    def getTableColumns(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        getTableColumnsOnLinks(table, model.allModelLinks)
}