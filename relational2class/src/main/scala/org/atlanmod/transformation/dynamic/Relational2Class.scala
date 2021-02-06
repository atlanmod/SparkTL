package org.atlanmod.transformation.dynamic

import org.atlanmod.model.classmodel.ClassDatatype
import org.atlanmod.model.relationalmodel.{RelationalColumn, RelationalMetamodel, RelationalModel, RelationalTable, RelationalType}
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}

object Relational2Class {

    def class2relational(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        val rmm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "Type2DataType",
                    types = List(RelationalMetamodel.TYPE),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "type",
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None
                                else {
                                    val type_ = l.head.asInstanceOf[RelationalType]
                                    Some(new ClassDatatype(type_.getId, type_.getName))
                                }
                        )
                    )
                ), // Type2Datatype
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "Table2Class",
                    types = List(RelationalMetamodel.TABLE),
                    from = (_, t) => Some(t.head.asInstanceOf[RelationalTable].getName.indexOf('_') == -1),
                    to = List(
                        // TODO
                    )), // Table2Class
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "Column2Attribute",
                    types = List(RelationalMetamodel.COLUMN),
                    from = (sm, c) =>
                        Some(
                            RelationalMetamodel.isNotKeyOf(
                                c.head.asInstanceOf[RelationalColumn],
                                sm.asInstanceOf[RelationalModel]
                            )
                        ),
                    to = List(
                        // TODO
                    )), // Column2Attribute
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "Multivalued",
                    types = List(RelationalMetamodel.TABLE, RelationalMetamodel.TABLE),
                    from = (_, ts) => {
                        val t1 = ts.head.asInstanceOf[RelationalTable]
                        val t2 = ts(1).asInstanceOf[RelationalTable]
                        Some(t1.getName.indexOf('_') != -1 && t2.getName.startsWith(t1.getName))
                    },
                    to = List(
                        // TODO
                    )), // Multivalued
            )
        )
    }

}
