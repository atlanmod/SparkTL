package org.atlanmod.transformation.dynamic

import org.atlanmod.model.dynamic.classModel.{ClassAttribute, ClassClass, ClassMetamodel}
import org.atlanmod.model.dynamic.relationalModel._
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

import scala.collection.JavaConverters

object Class2Relational {

    def transformation(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        val rmm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    types = List(ClassMetamodel.CLASS),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _) => Some(1), // No iterator
                    to = List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            elementExpr = (_, _, l) => {
                                if (l.isEmpty) None
                                else {
                                    val _class = l.head.asInstanceOf[ClassClass]
                                    Some(new RelationalTable(_class.getId, _class.getName))
                                }
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, t) => {
                                        val attrs = JavaConverters.asScalaBuffer(
                                            c.head.asInstanceOf[ClassClass].getAttributes()).toList

//                                        None
                                        val cols = Resolve.resolveAll(tls, sm, rmm, "col",
                                            RelationalMetamodel.COLUMN , ListUtils.singletons(attrs))

                                        cols match {
                                            case Some(lcols) =>
                                                Some(new TableToColumns(
                                                    t.asInstanceOf[RelationalTable],
                                                    lcols.asInstanceOf[List[RelationalColumn]])
                                                )
                                            case None => None
                                        }

                                    }
                                )
                            )
                        ))
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(!l.head.asInstanceOf[ClassAttribute].isDerived),
                    itExpr = (_, _) => Some(1), // No where clause
                    to = List(
                        new OutputPatternElementImpl(
                            name = "col",
                            elementExpr = (_, _, l) => {
                                if (l.isEmpty) None
                                else {
                                    val attribute = l.head.asInstanceOf[ClassAttribute]
                                    Some(new RelationalColumn(attribute.getId, attribute.getName))
                                }
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, a, c) => {
                                        val cl = a.head.asInstanceOf[ClassAttribute].getClass_

//                                        None
                                        val tb = Resolve.resolve(tls, sm, rmm, "tab",
                                            RelationalMetamodel.TABLE, List(cl))
                                        tb match {
                                            case Some(table) =>
                                                Some(new ColumnToTable(
                                                    c.asInstanceOf[RelationalColumn],
                                                    table.asInstanceOf[RelationalTable])
                                                )
                                            case None => None
                                        }

                                    }
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}
