package org.atlanmod.transformation.dynamic

import org.atlanmod.model.dynamic.classModel.{ClassAttribute, ClassClass, ClassMetamodel}
import org.atlanmod.model.dynamic.relationalModel._
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

import scala.collection.JavaConverters

object Class2RelationalMV {

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
                                        val cl = c.head.asInstanceOf[ClassClass]
                                        val attrs = JavaConverters.asScalaBuffer(cl.getAttributes()).toList
                                        val cols = Resolve.resolveAll(tls, sm, rmm, "col",
                                            RelationalMetamodel.COLUMN , ListUtils.singletons(attrs))
                                        val key = Resolve.resolve(tls, sm, rmm, "key",
                                            RelationalMetamodel.COLUMN, List(cl))

                                        (cols, key) match {
                                            case (Some(lcols), Some(k)) =>
                                                Some(new TableToColumns(
                                                    t.asInstanceOf[RelationalTable],
                                                    k.asInstanceOf[RelationalColumn] :: lcols.asInstanceOf[List[RelationalColumn]])
                                                )
                                            case _ => None
                                        }

                                    }
                                )
                            )
                        ), // tab
                        new OutputPatternElementImpl(
                            name = "key",
                            elementExpr = (_, _, l) => {
                                if (l.isEmpty) None
                                else {
                                    val _class = l.head.asInstanceOf[ClassClass]
                                    Some(new RelationalColumn(_class.getId, _class.getName + "id"))
                                }
                            },
                            outputElemRefs = List()) // key
                    )
                ), // Class2Table
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(!l.head.asInstanceOf[ClassAttribute].isMultivalued),
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
                        ) // col
                    )
                ), // Attribute2Column
                new RuleImpl(
                    name = "MultivaluedAttribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(l.head.asInstanceOf[ClassAttribute].isMultivalued),
                    itExpr = (_, _) => Some(1), // No iterator
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
                                        val att = a.head.asInstanceOf[ClassAttribute]
                                        val tb = Resolve.resolve(tls, sm, rmm, "pivot",
                                          RelationalMetamodel.TABLE, List(att))
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
                        ), // col
                        new OutputPatternElementImpl(
                            name = "pivot",
                            elementExpr = (_, sm, l) => {
                                if (l.isEmpty) None
                                else {
                                    val attribute = l.head.asInstanceOf[ClassAttribute]
                                    Some(new RelationalTable(attribute.getId, attribute.getName + "Pivot"))
                                }
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, i, sm, a, t) => {
                                        val att = a.head.asInstanceOf[ClassAttribute]
                                        val psrc = Resolve.resolve(tls, sm, rmm, "psrc", RelationalMetamodel.COLUMN,
                                            List(att))
                                        val ptrg = Resolve.resolve(tls, sm, rmm, "ptrg", RelationalMetamodel.COLUMN,
                                            List(att))
                                        (psrc, ptrg) match {
                                            case (Some(src), Some(trg)) => Some(new TableToColumns(
                                                t.asInstanceOf[RelationalTable],
                                                List(
                                                    src.asInstanceOf[RelationalColumn],
                                                    trg.asInstanceOf[RelationalColumn]
                                                )
                                            ))
                                            case _ => None
                                        }
                                    }
                                )
                            )
                        ), // pivot
                        new OutputPatternElementImpl(
                            name = "psrc",
                            elementExpr = (_, sm, l) => {
                                if (l.isEmpty) None
                                else {
                                    val attribute = l.head.asInstanceOf[ClassAttribute]
                                    Some(new RelationalColumn(attribute.getId, "key"))
                                }
                            },
                            outputElemRefs = List()
                        ), // psrc
                        new OutputPatternElementImpl(
                            name = "ptrg",
                            elementExpr = (_, sm, l) => {
                                if (l.isEmpty) None
                                else {
                                    val attribute = l.head.asInstanceOf[ClassAttribute]
                                    Some(new RelationalColumn(attribute.getId, attribute.getName))
                                }
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, i, sm, a, c) => {
                                        val cl = a.head.asInstanceOf[ClassAttribute].getClass_
                                        Resolve.resolve(tls, sm, rmm, "tab", RelationalMetamodel.TABLE, List(cl))
                                        match {
                                            case Some(tb) => Some(
                                                new ColumnToTable(
                                                    c.asInstanceOf[RelationalColumn],
                                                    tb.asInstanceOf[RelationalTable]
                                                ))
                                            case _ => None
                                        }
                                    }
                                )
                            )
                        ) // ptrg
                    )
                )  // MultivaluedAttribute2Column
            )
        )
    }
}
