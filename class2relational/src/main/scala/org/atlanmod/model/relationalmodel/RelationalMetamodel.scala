package org.atlanmod.model.relationalmodel

import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.Metamodel

import scala.annotation.tailrec

object RelationalMetamodel {

    def metamodel : Metamodel[DynamicElement, DynamicLink, String, String]
    = new DynamicMetamodel[DynamicElement, DynamicLink]()


    final val TABLE = "Table"
    final val COLUMN = "Column"
    final val TYPE = "Type"
    final val TABLE_COLUMNS = "columns"
    final val TABLE_KEY = "key"
    final val COLUMN_TABLE = "table"
    final val COLUMN_TYPE = "type"

    @tailrec
    private def getColumnOwnerOnLinks(c: RelationalColumn, l: List[RelationalLink]): Option[RelationalTable] =
        l match {
            case (h: ColumnToTable) :: l2 =>
                if (h.getSource.equals(c)) Some(h.getTargetTable)
                else getColumnOwnerOnLinks(c, l2)
            case _ :: l2 => getColumnOwnerOnLinks(c, l2)
            case List() => None
        }

    def getColumnOwner(c: RelationalColumn, model: RelationalModel): Option[RelationalTable] =
        getColumnOwnerOnLinks(c, model.allModelLinks)

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

    @tailrec
    private def getSVColumnsOfTableOnLinks(table: RelationalTable, allModelLinks: List[RelationalLink], model: RelationalModel)
    : Option[List[RelationalColumn]] =
        allModelLinks match {
            case (h: TableToColumns) :: l2 =>
                if (h.getSource.equals(table)) Some(h.getTarget.filter(col => RelationalMetamodel.isNotKeyOf(col, model)))
                else getSVColumnsOfTableOnLinks(table, l2, model)
            case _ :: l2 => getSVColumnsOfTableOnLinks(table, l2, model)
            case List() => None
        }


    def getSVColumnsOfTable(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        getSVColumnsOfTableOnLinks(table, model.allModelLinks, model)

    @tailrec
    private def getMVTablesOfTableOnElements(table: RelationalTable, allModelElements: List[RelationalElement],
                                     acc: List[List[RelationalTable]] = List())
    : List[List[RelationalTable]] = {
        allModelElements match {
            case (h: RelationalTable) :: l2 =>
                val new_acc =
                    if (h.getName.startsWith(table.getName) & h != table)
                        List(table, h) :: acc
                    else acc
                getMVTablesOfTableOnElements(table, l2, new_acc)
            case _ :: l2 => getMVTablesOfTableOnElements(table, l2, acc)
            case List() => acc
        }
    }

    def getMVTablesOfTable(table: RelationalTable, model: RelationalModel): Option[List[List[RelationalTable]]] = {
        if (table.getName.indexOf("_") != -1) None
        getMVTablesOfTableOnElements(table, model.allModelElements) match {
            case l if l.nonEmpty => Some(l)
            case List() => None
        }
    }

    @tailrec
    private def isKeyOfOnLinks(column: RelationalColumn, links: List[RelationalLink]) : Boolean = {
        links match {
            case (h: TableToKeys) :: l2 =>
                if (h.getTarget.contains(column)) true
                else isKeyOfOnLinks(column, l2)
            case _ :: l2 => isKeyOfOnLinks(column, l2)
            case List() => false
        }
    }

    def isKeyOf(c: RelationalColumn, model: RelationalModel): Boolean  =
        isKeyOfOnLinks(c, model.allModelLinks)

    def isNotKeyOf(c: RelationalColumn, model: RelationalModel): Boolean  =
        !isKeyOf(c, model)

    @tailrec
    private def getColumnTypeOnLinks(column: RelationalColumn, links: List[RelationalLink]): Option[RelationalTypable] =
        links match {
            case (h: ColumnToType) :: l2 =>
                if (h.getSource.equals(column)) Some(h.getTargetType)
                else getColumnTypeOnLinks(column, l2)
            case _ :: l2 => getColumnTypeOnLinks(column, l2)
            case _ => None
        }

    def getColumnType(column: RelationalColumn, model: RelationalModel): Option[RelationalTypable]  =
        getColumnTypeOnLinks(column, model.allModelLinks)

    private def tryGetATypeOnLinks(h: RelationalColumn, links: List[RelationalLink]): Option[RelationalTypable] =
        links.find(l => l.getSource.equals(h) && l.isInstanceOf[ColumnToType]) match {
            case Some(l: ColumnToType) => Some(l.getTargetType)
            case _ => None
        }

    private def tryGetAType(column: RelationalColumn, model: RelationalModel): Option[RelationalTypable] =
        tryGetATypeOnLinks(column, model.allModelLinks)

    @tailrec
    private def getAType(columns: List[RelationalColumn], model: RelationalModel): Option[RelationalTypable] =
        columns match {
            case h :: t =>
                tryGetAType(h, model) match {
                    case Some(type_) => Some(type_)
                    case _ => getAType(t, model)
                }
            case List() => None
        }

    def getMVTableType(table: RelationalTable, model: RelationalModel): Option[RelationalTypable] = {
        getTableColumns(table, model) match {
            case Some(columns) => getAType(columns, model)
            case _ => None
        }
    }
}