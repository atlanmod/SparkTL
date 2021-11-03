package org.atlanmod.class2relational.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.class2relational.model.classmodel._
import org.atlanmod.class2relational.model.relationalmodel._
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{Model, TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

object Class2Relational {

    val random = scala.util.Random

    final val PATTERN_TABLE : String = "tab"
    final val PATTERN_SVCOLUMNS : String = "svcol"
    final val PATTERN_TYPE : String = "type"
    final val PATTERN_KEY: String = "key"
    final val PATTERN_PIVOT: String = "pivot"
    final val PATTERN_PIVOT_SOURCE: String = "psrc"
    final val PATTERN_PIVOT_TARGET: String = "ptrg"

    final val dynamic_mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def makeTableToSVColumnsWithKey(tls: TraceLinks[DynamicElement, DynamicElement], model: ClassModel,
                                    class_ : ClassClass, table: RelationalTable): Option[TableToColumns] = {
        ClassMetamodel.getClassAttributes(class_, model) match {
            case Some(attributes) =>
                val cols = Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_SVCOLUMNS, RelationalMetamodel.COLUMN,
                    ListUtils.singletons(attributes))
                val key = Resolve.resolve(tls, model, dynamic_mm, PATTERN_KEY, RelationalMetamodel.COLUMN, List(class_))
                (cols, key) match {
                    case (Some(lcols), Some(k)) =>
                        Some(new TableToColumns(
                            table, k.asInstanceOf[RelationalColumn] :: lcols.asInstanceOf[List[RelationalColumn]])
                        )
                    case _ => None
                }
            case _ => None
        }
    }

    def makeTableToKey(tls: TraceLinks[DynamicElement, DynamicElement], model: ClassModel, class_ : ClassClass, table: RelationalTable)
    : Option[TableToKeys] = {
        Resolve.resolve(tls, model, dynamic_mm, PATTERN_KEY, RelationalMetamodel.COLUMN, List(class_)) match {
            case Some(k) =>
                Some(new TableToKeys(table, k.asInstanceOf[RelationalColumn]))
            case _ => None
        }
    }

    def makeSVColumnToTable(tls: TraceLinks[DynamicElement, DynamicElement], model: ClassModel, attribute: ClassAttribute,
                            column: RelationalColumn): Option[ColumnToTable] = {
        ClassMetamodel.getAttributeOwner(attribute, model) match {
            case Some(owner) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_TABLE, RelationalMetamodel.TABLE, List(owner)) match {
                    case Some(table) =>
                        Some(new ColumnToTable(column, table.asInstanceOf[RelationalTable]))
                    case _ => None
                }
            case _ => None
        }
    }

    def makeColumnToType(tls: TraceLinks[DynamicElement, DynamicElement], model: ClassModel, attribute: ClassAttribute,
                         column: RelationalColumn): Option[ColumnToType] = {
        ClassMetamodel.getAttributeType(attribute, model) match {
            case Some(cl : ClassClass) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_TABLE, RelationalMetamodel.TABLE, List(cl)) match {
                    case Some(table) => Some(new ColumnToType(column, table.asInstanceOf[RelationalTable]))
                    case _ => None
                }
            case Some(dt : ClassDatatype) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_TYPE, RelationalMetamodel.TYPE, List(dt)) match {
                    case Some(type_) => Some(new ColumnToType(column, type_.asInstanceOf[RelationalType]))
                    case _ => None
                }
            case _ => None
        }
    }

    def findPivotSrcTrg(tls: TraceLinks[DynamicElement, DynamicElement], model: Model[DynamicElement, DynamicLink],
                        attribute: ClassAttribute)
    : Option[(RelationalColumn, RelationalColumn)] = {
        val psrc = Resolve.resolve(tls, model, dynamic_mm, PATTERN_PIVOT_SOURCE, RelationalMetamodel.COLUMN, List(attribute))
        val ptrg = Resolve.resolve(tls, model, dynamic_mm, PATTERN_PIVOT_TARGET, RelationalMetamodel.COLUMN, List(attribute))
        (psrc, ptrg) match {
            case (Some(src), Some(trg)) => Some((src.asInstanceOf[RelationalColumn], trg.asInstanceOf[RelationalColumn]))
            case _ => None
        }
    }

    def makeMVTableToColumns(tls: TraceLinks[DynamicElement, DynamicElement],
                             model: Model[DynamicElement, DynamicLink], attribute: ClassAttribute,
                             table: RelationalTable): Option[TableToColumns] = {
        findPivotSrcTrg(tls, model, attribute) match {
            case Some((src, trg)) => Some(new TableToColumns(table,
                List(src.asInstanceOf[RelationalColumn], trg.asInstanceOf[RelationalColumn])))
            case _ => None
        }
    }

    def makeMVTableToKeys(tls: TraceLinks[DynamicElement, DynamicElement], model: Model[DynamicElement, DynamicLink],
                          attribute: ClassAttribute,
                             table: RelationalTable): Option[TableToKeys] = {
        findPivotSrcTrg(tls, model, attribute) match {
            case Some((src, trg)) => Some(new TableToKeys(table, List(src, trg)))
            case _ => None
        }
    }

    def makeColumnToMVTable(tls: TraceLinks[DynamicElement, DynamicElement], model: ClassModel,
                            attribute: ClassAttribute, column: RelationalColumn): Option[ColumnToTable] = {
        Resolve.resolve(tls, model, dynamic_mm, PATTERN_PIVOT, RelationalMetamodel.TABLE, List(attribute)) match {
            case Some(table) => Some(new ColumnToTable(column, table.asInstanceOf[RelationalTable]))
            case _ => None
        }
    }

    def class2relational(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] =
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
        List(
            new RuleImpl(
                name = "DataType2Type",
                types = List(ClassMetamodel.DATATYPE),
                from = (m, l) => {
                    my_sleep(sleeping_guard, random.nextInt())
                    Some(true)
                },
                to = List(new OutputPatternElementImpl(name = PATTERN_TYPE,
                    elementExpr = (_, _, l) =>
                        if (l.isEmpty) None else {
                            my_sleep(sleeping_instantiate, random.nextInt())
                            val datatype = l.head.asInstanceOf[ClassDatatype]
                            Some(new RelationalType(datatype.getId, datatype.getName))
                        }
                    ))
            ), // DataType2Type
            new RuleImpl(
                name = "Class2Table",
                types = List(ClassMetamodel.CLASS),
                from = (m, l) => {
                    my_sleep(sleeping_guard, random.nextInt())
                    Some(true)
                },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_TABLE,
                        elementExpr = (_, _, l) =>
                            if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val class_ = l.head.asInstanceOf[ClassClass]
                                Some(new RelationalTable(class_.getId, class_.getName))
                            },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, cl, tb) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeTableToSVColumnsWithKey(tls, sm.asInstanceOf[ClassModel],
                                        cl.head.asInstanceOf[ClassClass], tb.asInstanceOf[RelationalTable])
                                }
                            ), // table to sv columns
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, cl, tb) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeTableToKey(tls, sm.asInstanceOf[ClassModel],
                                        cl.head.asInstanceOf[ClassClass], tb.asInstanceOf[RelationalTable])
                                }
                            ) // table to key
                        )
                    ), // table
                    new OutputPatternElementImpl(name = PATTERN_KEY,
                        elementExpr = (_, _, l) =>
                            if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val class_ = l.head.asInstanceOf[ClassClass]
                                Some(new RelationalColumn(class_.getId + "Id", "Id"))
                            },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, class_, column) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    Resolve.resolve(tls, sm, dynamic_mm, PATTERN_TABLE, RelationalMetamodel.TABLE,
                                        List(class_.head)) match {
                                        case Some(table) =>
                                            Some(new ColumnToTable(column.asInstanceOf[RelationalColumn],
                                                table.asInstanceOf[RelationalTable]))
                                        case _ => None
                                    }
                                }
                            )
                        )
                    ) // key
                )
            ), // Class2Table
            new RuleImpl(
                name = "SVAttribute2Column",
                types = List(ClassMetamodel.ATTRIBUTE),
                from = (_, l) => {
                    my_sleep(sleeping_guard, random.nextInt())
                    Some(!l.head.asInstanceOf[ClassAttribute].isMultivalued)
                },
                to = List(
                    new OutputPatternElementImpl(
                        name = PATTERN_SVCOLUMNS,
                        elementExpr = (_, _, l) =>
                            if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val attribute = l.head.asInstanceOf[ClassAttribute]
                                Some(new RelationalColumn(attribute.getId, attribute.getName))
                            },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, c) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeSVColumnToTable(tls, sm.asInstanceOf[ClassModel],
                                        a.head.asInstanceOf[ClassAttribute], c.asInstanceOf[RelationalColumn])
                                }
                            ), // to its owner
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, c) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeColumnToType(tls, sm.asInstanceOf[ClassModel],
                                        a.head.asInstanceOf[ClassAttribute], c.asInstanceOf[RelationalColumn])
                                }
                            ) // to its type
                        )
                    )
                )
            ), // SVAttribute2Column
            new RuleImpl(
                name = "MVAttribute2Column",
                types = List(ClassMetamodel.ATTRIBUTE),
                from = (_, l) => {
                    my_sleep(sleeping_guard, random.nextInt())
                    Some(l.head.asInstanceOf[ClassAttribute].isMultivalued)
                },
                to = List(
                    new OutputPatternElementImpl(
                        name = PATTERN_PIVOT,
                        elementExpr = (_, sm, l) =>
                          if (l.isEmpty) None else {
                              my_sleep(sleeping_instantiate, random.nextInt())
                              val attribute = l.head.asInstanceOf[ClassAttribute]
                              ClassMetamodel.getAttributeOwner(attribute, sm.asInstanceOf[ClassModel]) match {
                                  case Some(owner) =>
                                      Some(new RelationalTable(attribute.getId + "pivot",
                                          owner.getName + "_" + attribute.getName))
                                  case _ => None
                              }
                          },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, t) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeMVTableToColumns(tls, sm, a.head.asInstanceOf[ClassAttribute],
                                            t.asInstanceOf[RelationalTable])
                                    }), // table to columns
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, t) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeMVTableToKeys(tls, sm,
                                        a.head.asInstanceOf[ClassAttribute], t.asInstanceOf[RelationalTable])
                                }
                            ) // table to key
                        )
                    ), // table pivot
                    new OutputPatternElementImpl(
                        name = PATTERN_PIVOT_SOURCE,
                        elementExpr = (_, _, l) =>
                          if (l.isEmpty) None
                          else {
                              my_sleep(sleeping_instantiate, random.nextInt())
                              Some(new RelationalColumn(l.head.asInstanceOf[ClassAttribute].getId + "psrc", "Id"))
                          },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, c) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeColumnToMVTable(tls, sm.asInstanceOf[ClassModel], a.head.asInstanceOf[ClassAttribute],
                                        c.asInstanceOf[RelationalColumn])
                                }
                            )
                        )
                    ), // col source
                    new OutputPatternElementImpl(
                        name = PATTERN_PIVOT_TARGET,
                        elementExpr = (_, sm, l) =>
                          if (l.isEmpty) None else {
                              my_sleep(sleeping_instantiate, random.nextInt())
                              val att = l.head.asInstanceOf[ClassAttribute]
                              ClassMetamodel.getAttributeType(att, sm.asInstanceOf[ClassModel]) match {
                                  case Some(type_) =>
                                      Some(new RelationalColumn(att.getId + "ptrg", type_.getName))
                                  case _ => None
                              }
                          },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, c) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeColumnToMVTable(tls, sm.asInstanceOf[ClassModel], a.head.asInstanceOf[ClassAttribute],
                                            c.asInstanceOf[RelationalColumn])
                                }
                            ),
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, a, c) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    makeColumnToType(tls, sm.asInstanceOf[ClassModel], a.head.asInstanceOf[ClassAttribute],
                                        c.asInstanceOf[RelationalColumn])
                                }
                            )
                        )
                    ) // col target
                )
            ) // MVAttribute2Column
        )
    )

}