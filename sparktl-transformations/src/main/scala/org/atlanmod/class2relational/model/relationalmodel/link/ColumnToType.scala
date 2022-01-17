package org.atlanmod.class2relational.model.relationalmodel.link

import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalClassifier}
import org.atlanmod.class2relational.model.relationalmodel.RelationalLink
import org.atlanmod.class2relational.model.relationalmodel.metamodel.RelationalMetamodelNaive

class ColumnToType(source: RelationalColumn, target: RelationalClassifier)
  extends RelationalLink(RelationalMetamodelNaive.COLUMN_TYPE, source, List(target)){

    override def toString: String =
        "(" + source.getId + ", " + getType + ", " + target.getId + ")"

    override def getSource: RelationalColumn = source

    override def getTarget: List[RelationalClassifier] =  List(target)

    def getTargetType : RelationalClassifier = target

}

