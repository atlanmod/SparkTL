package org.atlanmod.class2relational.model.relationalmodel.metamodel
import org.atlanmod.class2relational.model.relationalmodel.RelationalModel
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalClassifier, RelationalType}

object RelationalMetamodelWithMap extends RelationalMetamodel{

    override def getTableColumns(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]] =
        metamodel.allLinksOfTypeOfElement(table, TABLE_COLUMNS, model) match {
            case Some(e: List[RelationalColumn]) => Some(e)
            case _ => None
        }

    override def getTableKeys(table: RelationalTable, model: RelationalModel): Option[List[RelationalColumn]]=
        metamodel.allLinksOfTypeOfElement(table, TABLE_KEYS, model) match {
            case Some(e: List[RelationalColumn]) => Some(e)
            case _ => None
        }

    override def getKeyOf(c: RelationalColumn, model: RelationalModel): Option[RelationalTable] =
        model.allModelLinks.find(l => l.getType.equals(TABLE_KEYS) && l.getTarget.contains(c)).map(l => l.getSource.asInstanceOf[RelationalTable])

    override def getColumnType(column: RelationalColumn, model: RelationalModel): Option[RelationalClassifier] =
        metamodel.allLinksOfTypeOfElement(column, COLUMN_TYPE, model) match {
            case Some(e: List[RelationalClassifier]) => Some(e.head)
            case _ => None
        }

    override def getAllTable(model: RelationalModel): List[RelationalTable] =
        model.allModelElements.filter(r => r.getType.equals(TABLE)).map(de => de.asInstanceOf[RelationalTable])

    override def getAllColumns(model: RelationalModel): List[RelationalColumn] =
        model.allModelElements.filter(r => r.getType.equals(COLUMN)).map(de => de.asInstanceOf[RelationalColumn])

    override def getAllType(model: RelationalModel): List[RelationalType] =
        model.allModelElements.filter(r => r.getType.equals(TYPE)).map(de => de.asInstanceOf[RelationalType])

    override def getAllTypable(model: RelationalModel): List[RelationalClassifier] =
        getAllTable(model) ++ getAllType(model)

}
