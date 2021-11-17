package org.atlanmod.class2relational.model.relationalmodel

import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}

import scala.annotation.tailrec

object RelationalMetamodel {


    def metamodel : DynamicMetamodel[DynamicElement, DynamicLink]
    = new DynamicMetamodel[DynamicElement, DynamicLink]("RelationalMetamodel")

    final val TABLE = "Table"
    final val COLUMN = "Column"
    final val TYPE = "Type"
    final val TABLE_COLUMNS = "columns"
    final val TABLE_KEY = "key"
    final val COLUMN_TABLE = "table"
    final val COLUMN_TYPE = "type"

    private def getMVTablesOfTableOnElementsOld(table: RelationalTable, allModelElements: Iterator[RelationalElement])
    : List[List[RelationalTable]] = {
        var acc: List[List[RelationalTable]] = List()
        while(allModelElements.hasNext){
            val v = allModelElements.next()
            v match {
                case table1: RelationalTable =>
                    acc =
                      if (table1.getName.startsWith(table.getName) & table1 != table)
                          List(table, table1) :: acc
                      else acc
                case _ =>
            }
        }
        acc
    }


    def getMVTablesOfTableOld(table: RelationalTable, model: RelationalModel): Option[List[List[RelationalTable]]] = {
        if (table.getName.indexOf("_") != -1) None
        getMVTablesOfTableOnElementsOld(table, model.allModelElements) match {
            case l if l.nonEmpty => Some(l)
            case List() => None
        }
    }

    private def getColumnOwnerOnLinks(c: RelationalColumn, it: Iterator[RelationalLink]): Option[RelationalTable] = {
        if (it.hasNext){
            val v = it.next()
            if (v.getSource.equals(c) && v.isInstanceOf[ColumnToTable])
                return Some(v.asInstanceOf[ColumnToTable].getTargetTable)
        }
        None
    }

    def getColumnOwner(c: RelationalColumn, model: RelationalModel): Option[RelationalTable] =
        getColumnOwnerOnLinks(c, model.allModelLinks)

    private def getTableColumnsOnLinks(t: RelationalTable, l: Iterator[RelationalLink]): Option[List[RelationalColumn]] = {
        while(l.hasNext){
            l.next() match {
                case h: TableToColumns =>
                    if (h.getSource.equals(t)) Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    def getTableColumns(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        getTableColumnsOnLinks(table, model.allModelLinks)

    private def getSVColumnsOfTableOnLinks(table: RelationalTable, l: Iterator[RelationalLink], model: RelationalModel)
    : Option[List[RelationalColumn]] = {
        while(l.hasNext){
            l.next() match {
                case h: TableToColumns =>
                    if (h.getSource.equals(table)) Some(h.getTarget.filter(col => RelationalMetamodel.isNotAKey(col, model)))
                case _ =>
            }
        }
        None
    }

    def getSVColumnsOfTable(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        getSVColumnsOfTableOnLinks(table, model.allModelLinks, model)

    private def getMVTablesOfTableOnElements(table: RelationalTable, elements: Iterator[RelationalElement],
                                     )
    : List[RelationalTable] = {
        var acc: List[RelationalTable] = List()
        while(elements.hasNext){
            elements.next() match {
                case h: RelationalTable =>
                    acc =
                      if (h.getName.startsWith(table.getName) & h != table)
                            h :: acc
                        else acc
                case _ =>
            }
        }
        acc
    }

    def getMVTablesOfTable(table: RelationalTable, model: RelationalModel): Option[List[RelationalTable]] = {
        if (table.getName.indexOf("_") != -1) None
        getMVTablesOfTableOnElements(table, model.allModelElements) match {
            case l if l.nonEmpty => Some(l)
            case List() => None
        }
    }

    private def isAKeyOnLinks(column: RelationalColumn, links: Iterator[RelationalLink]) : Boolean = {
        while(links.hasNext){
            links.next() match{
                case h: TableToKeys =>
                    if (h.getTarget.contains(column)) true
                case _ =>
            }
        }
        false
    }

    def isAKey(c: RelationalColumn, model: RelationalModel): Boolean  =
        isAKeyOnLinks(c, model.allModelLinks)

    def isNotAKey(c: RelationalColumn, model: RelationalModel): Boolean  =
        !isAKey(c, model)


    private def isKeyOfOnLinks(column: RelationalColumn, table: RelationalTable, links: Iterator[RelationalLink]): Boolean = {
        while(links.hasNext){
            links.next() match {
                case h: TableToKeys =>
                    if (h.getSource.equals(table) & h.getTarget.contains(column)) true
                case _ =>
            }
        }
        false
    }

    def isKeyOf(c: RelationalColumn, t: RelationalTable, model: RelationalModel): Boolean =
        isKeyOfOnLinks(c, t, model.allModelLinks)

    def isNotKeyOf(c: RelationalColumn, t: RelationalTable, model: RelationalModel): Boolean =
        !isKeyOf(c, t, model)


    private def getColumnTypeOnLinks(column: RelationalColumn, links: Iterator[RelationalLink]): Option[RelationalTypable] = {
        while(links.hasNext){
            links.next() match {
                case h: ColumnToType => if (h.getSource.equals(column)) Some(h.getTargetType)
                case _ =>
            }
        }
        None
    }

    def getColumnType(column: RelationalColumn, model: RelationalModel): Option[RelationalTypable]  =
        getColumnTypeOnLinks(column, model.allModelLinks)

    private def tryGetATypeOnLinks(h: RelationalColumn, links: Iterator[RelationalLink]): Option[RelationalTypable] =
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

    def getAllTable(model: RelationalModel): List[RelationalTable] = {
        model.allModelElements.filter(r => r.getType.equals(TABLE)).map(de => de.asInstanceOf[RelationalTable]).toList
    }

    def getAllColumns(model: RelationalModel): List[RelationalColumn] = {
        model.allModelElements.filter(r => r.getType.equals(COLUMN)).map(de => de.asInstanceOf[RelationalColumn]).toList
    }

    def getAllType(model: RelationalModel): List[RelationalType] = {
        model.allModelElements.filter(r => r.getType.equals(TYPE)).map(de => de.asInstanceOf[RelationalType]).toList
    }

    def getAllTypable(model: RelationalModel): List[RelationalTypable] = {
        getAllTable(model).asInstanceOf[List[RelationalTypable]] ++ getAllType(model).asInstanceOf[List[RelationalTypable]]
    }
}