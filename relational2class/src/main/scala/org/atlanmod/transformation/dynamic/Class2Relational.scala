package org.atlanmod.transformation.dynamic

import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.model.classmodel.{ClassAttribute, ClassClass, ClassDatatype, ClassMetamodel, ClassModel}
import org.atlanmod.model.relationalmodel.{RelationalColumn, RelationalMetamodel, RelationalTable, RelationalType, TableToColumns, TableToKeys}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

object Class2Relational {
    
    def class2relational(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        val rmm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "Class2Table",
                    types = List(ClassMetamodel.CLASS),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            elementExpr = (_, _, l) =>
                              if (l.isEmpty) None
                              else {
                                  val _class = l.head.asInstanceOf[ClassClass]
                                  Some(new RelationalTable(_class.getId, _class.getName))
                              },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, t) => {
                                        val cl = c.head.asInstanceOf[ClassClass]
                                        ClassMetamodel.getClassAttributes(cl, sm.asInstanceOf[ClassModel])  match {
                                            case Some(attrs) =>
                                                val cols = Resolve.resolveAll(tls, sm, rmm, "svcol",
                                                    RelationalMetamodel.COLUMN , ListUtils.singletons(attrs))
                                                val key = Resolve.resolve(tls, sm, rmm, "key",
                                                    RelationalMetamodel.COLUMN, List(cl))
                                                (cols, key) match {
                                                    case (Some(lcols), Some(k)) =>
                                                        Some(new TableToColumns(
                                                            t.asInstanceOf[RelationalTable],
                                                            k.asInstanceOf[RelationalColumn] ::
                                                              lcols.asInstanceOf[List[RelationalColumn]])
                                                        )
                                                    case _ => None
                                                }
                                            case _ => None
                                        }

                                    }
                                ), // to columns (not mv)
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, t) => {
                                        val cl = c.head.asInstanceOf[ClassClass]
                                        Resolve.resolve(tls, sm, rmm, "key", RelationalMetamodel.COLUMN, List(cl))
                                        match {
                                            case Some(k) =>
                                                Some(new TableToKeys(
                                                    t.asInstanceOf[RelationalTable],
                                                    k.asInstanceOf[RelationalColumn])
                                                )
                                            case _ => None
                                        }
                                    }
                                ) // to key
                            )
                        ), // table
                        new OutputPatternElementImpl(
                            name = "key",
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None
                                else {
                                    val _class = l.head.asInstanceOf[ClassClass]
                                    Some(new RelationalColumn(_class.getId + "Id", "Id"))
                                }
                        ) // key
                    )
                ), // Class2Table
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "SVAttribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(!l.head.asInstanceOf[ClassAttribute].isMultivalued),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "svcol",
                            elementExpr = (_, _, l) =>
                              if (l.isEmpty) None
                              else {
                                  val attribute = l.head.asInstanceOf[ClassAttribute]
                                  Some(new RelationalColumn(attribute.getId, attribute.getName))
                              }
                        )
                    )

                ), // SVAttribute2Column
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "MVAttribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(l.head.asInstanceOf[ClassAttribute].isMultivalued),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "pivot",
                            elementExpr = (_, sm, l) =>
                              if (l.isEmpty) None
                              else {
                                  val attribute = l.head.asInstanceOf[ClassAttribute]
                                  ClassMetamodel.getAttributeType(attribute, sm.asInstanceOf[ClassModel]) match {
                                      case Some(owner) =>
                                          Some(new RelationalTable(attribute.getId + "pivot", owner.getName + "_" + attribute.getName))
                                      case _ => None
                                  }
                              },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, a, t) => {
                                        val attribute = a.head.asInstanceOf[ClassAttribute]
                                        val psrc = Resolve.resolve(tls, sm, rmm, "psrc", RelationalMetamodel.COLUMN,
                                            List(attribute))
                                        val ptrg = Resolve.resolve(tls, sm, rmm, "ptrg", RelationalMetamodel.COLUMN,
                                            List(attribute))
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
                                ), // to col source and target
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, a, t) => {
                                        val attribute = a.head.asInstanceOf[ClassAttribute]
                                        val psrc = Resolve.resolve(tls, sm, rmm, "psrc", RelationalMetamodel.COLUMN,
                                            List(attribute))
                                        val ptrg = Resolve.resolve(tls, sm, rmm, "ptrg", RelationalMetamodel.COLUMN,
                                            List(attribute))
                                        (psrc, ptrg) match {
                                            case (Some(src), Some(trg)) => Some(new TableToKeys(
                                                t.asInstanceOf[RelationalTable],
                                                List(
                                                    src.asInstanceOf[RelationalColumn],
                                                    trg.asInstanceOf[RelationalColumn]
                                                )
                                            ))
                                            case _ => None
                                        }
                                    }
                                ) // key to col source and target
                            )
                        ), // table pivot
                        new OutputPatternElementImpl(
                            name = "psrc",
                            elementExpr = (_, _, l) =>
                              if (l.isEmpty) None
                              else {
                                  val att = l.head.asInstanceOf[ClassAttribute]
                                  Some(new RelationalColumn(att.getId + "psrc",  "Id"))
                              }
                        ), // col source
                        new OutputPatternElementImpl(
                            name = "ptrg",
                            elementExpr = (_, sm, l) =>
                                if (l.isEmpty) None
                                else {
                                    val att = l.head.asInstanceOf[ClassAttribute]
                                    ClassMetamodel.getAttributeDatatype(att, sm.asInstanceOf[ClassModel]) match {
                                        case Some(datatype) =>
                                            Some(new RelationalColumn(att.getId + "ptrg", datatype.getName))
                                        case _ => None
                                    }
                                }
                        ), // col target
                    )
                ), // MVAttribute2Column
                new RuleImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
                    name = "DataType2Type",
                    types = List(ClassMetamodel.DATATYPE),
                    to = List(
                        new OutputPatternElementImpl(
                            name = "type",
                            elementExpr = (_, _, l) =>
                                if (l.isEmpty) None
                                else {
                                    val datatype = l.head.asInstanceOf[ClassDatatype]
                                    Some(new RelationalType(datatype.getId, datatype.getName))
                                }
                        )
                    )
                ) // DataType2Type
            )
        )
    }
}