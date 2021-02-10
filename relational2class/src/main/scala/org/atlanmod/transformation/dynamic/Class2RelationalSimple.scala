package org.atlanmod.transformation.dynamic

import org.atlanmod.model.classmodel.{ClassAttribute, ClassClass, ClassMetamodel, ClassModel}
import org.atlanmod.model.relationalmodel.{ColumnToTable, RelationalColumn, RelationalMetamodel, RelationalTable, TableToColumns}
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

object Class2RelationalSimple {
    def class2relational(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        val rmm = new DynamicMetamodel[DynamicElement, DynamicLink]()

        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    types = List(ClassMetamodel.CLASS),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            elementExpr =
                              (_, _, l) =>
                                if (l.isEmpty) None
                                else {
                                    val class_ = l.head.asInstanceOf[ClassClass]
                                    Some(new RelationalTable(class_.getId,class_.getName))
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, t) => {
                                        val class_ = c.head.asInstanceOf[ClassClass]
                                        ClassMetamodel.getClassAttributes(class_, sm.asInstanceOf[ClassModel]) match {
                                            case Some(attrs) =>
                                                val cols = Resolve.resolveAll(tls, sm, rmm, "col",
                                                    RelationalMetamodel.COLUMN , ListUtils.singletons(attrs))
                                                cols match {
                                                    case Some(columns) if columns.nonEmpty =>
                                                        Some(new TableToColumns(
                                                            t.asInstanceOf[RelationalTable],
                                                            columns.asInstanceOf[List[RelationalColumn]]
                                                        ))
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "col",
                            elementExpr =
                              (_, _, l) =>
                                  if (l.isEmpty) None else{
                                      val att = l.head.asInstanceOf[ClassAttribute]
                                      Some(new RelationalColumn(att.getId, att.getName))
                                  },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, a, c) =>
                                        {
                                            val attribute = a.head.asInstanceOf[ClassAttribute]
                                            ClassMetamodel.getAttributeOwner(attribute, sm.asInstanceOf[ClassModel]) match {
                                                case Some(class_) =>
                                                    Resolve.resolve(tls, sm, rmm, "tab", RelationalMetamodel.TABLE, List(class_)) match {
                                                        case Some(table) =>
                                                            Some(new ColumnToTable(
                                                                c.asInstanceOf[RelationalColumn],
                                                                table.asInstanceOf[RelationalTable])
                                                            )
                                                        case _ => None
                                                    }
                                                case _ => None
                                            }
                                        }
                                )
                            )
                        )
                    )
                )
            ))
    }
}
