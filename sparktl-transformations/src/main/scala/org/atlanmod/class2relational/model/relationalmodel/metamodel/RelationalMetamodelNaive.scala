package org.atlanmod.class2relational.model.relationalmodel.metamodel

import org.atlanmod.class2relational.model.relationalmodel.{RelationalLink, RelationalModel}
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalClassifier, RelationalType}
import org.atlanmod.class2relational.model.relationalmodel.link.{ColumnToType, TableToColumns, TableToKeys}

object RelationalMetamodelNaive extends RelationalMetamodel {

    private def getTableColumnsOnLinks(t: RelationalTable, l: Iterator[RelationalLink]): Option[List[RelationalColumn]] = {
        while (l.hasNext) {
            val v = l.next()
            v match {
                case h: TableToColumns =>
                    if (h.getSource.equals(t)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    override def getTableColumns(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
            getTableColumnsOnLinks(table, model.allModelLinks.toIterator)

    private def getTableKeysOnLinks(table: RelationalTable, it: Iterator[RelationalLink]): Option[List[RelationalColumn]] = {
        while (it.hasNext) {
            val v = it.next()
            v match {
                case h : TableToKeys => if(h.getSource.equals(table)) return Some(h.getTarget)
                case _ =>
            }
        }
        None
    }

    override def getTableKeys(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        getTableKeysOnLinks(table, model.allModelLinks.toIterator)

    private def getColumnTypeOnLinks(column: RelationalColumn, it: Iterator[RelationalLink]): Option[RelationalClassifier] = {
        while (it.hasNext) {
            val v = it.next()
            v match {
                case h : ColumnToType => if(h.getSource.equals(column)) return Some(h.getTargetType)
                case _ =>
            }
        }
        None
    }

    override def getColumnType(column: RelationalColumn, model: RelationalModel): Option[RelationalClassifier] =
        getColumnTypeOnLinks(column, model.allModelLinks.toIterator)

    override def getKeyOf(c: RelationalColumn, model: RelationalModel): Option[RelationalTable] =
        model.allModelLinks.find(l => l.getType.equals(TABLE_KEYS) && l.getTarget.contains(c)).map(l => l.getSource.asInstanceOf[RelationalTable])

    override def getAllTable(model: RelationalModel): List[RelationalTable] =
        model.allModelElements.filter(r => r.getType.equals(TABLE)).map(de => de.asInstanceOf[RelationalTable])

    override def getAllColumns(model: RelationalModel): List[RelationalColumn] =
        model.allModelElements.filter(r => r.getType.equals(COLUMN)).map(de => de.asInstanceOf[RelationalColumn])

    override def getAllType(model: RelationalModel): List[RelationalType] =
        model.allModelElements.filter(r => r.getType.equals(TYPE)).map(de => de.asInstanceOf[RelationalType])

    override def getAllTypable(model: RelationalModel): List[RelationalClassifier] =
        getAllTable(model) ++ getAllType(model)
}
