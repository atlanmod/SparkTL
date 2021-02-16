package org.atlanmod.transformation.dynamic

import org.atlanmod.model.classmodel.{AttributeToClass, AttributeToType, ClassAttribute, ClassClass, ClassDatatype, ClassMetamodel, ClassToAttributes}
import org.atlanmod.model.relationalmodel._
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

object Relational2Class {

    final val PATTERN_TYPE : String = "type"
    final val PATTERN_CLASS : String = "class"
    final val PATTERN_SVATTRIBUTE : String = "svatt"
    final val PATTERN_MVATTRIBUTE : String = "mvatt"

    val dynamic_mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def makeClassToAttributes_SV_MV(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                                    table: RelationalTable, class_ : ClassClass): Option[ClassToAttributes] = {
        var svattributes: Option[List[ClassAttribute]] = None
        RelationalMetamodel.getSVColumnsOfTable(table, model) match {
            case Some(columns) =>
                Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_SVATTRIBUTE,
                    ClassMetamodel.ATTRIBUTE, ListUtils.singletons(columns)) match {
                    case Some(attributes: List[ClassAttribute]) =>
                        svattributes = Some(attributes)
                    case _ =>
                }
            case _ =>
        }

        var mvattributes: Option[List[ClassAttribute]] = None
        RelationalMetamodel.getMVTablesOfTable(table, model) match {
            case Some(tables) =>
                Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_MVATTRIBUTE,
                    ClassMetamodel.ATTRIBUTE, tables) match {
                    case Some(attributes: List[ClassAttribute]) =>
                        mvattributes = Some(attributes)
                }
        }

        (svattributes, mvattributes) match {
            case (Some(svattr), Some(mvattr)) => Some (new ClassToAttributes(class_, svattr ++ mvattr))
            case (Some(svattr), None) => Some (new ClassToAttributes(class_, svattr ))
            case (None, Some(mvattr)) => Some (new ClassToAttributes(class_, mvattr))
            case (None, None) => None
        }
    }

    def makeSVAttributeToType(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                              column: RelationalColumn, attribute: ClassAttribute)
    : Option[AttributeToType] = {
        RelationalMetamodel.getColumnType(column, model) match {
            // the type is a concrete type (e.g., Integer)
            case Some(type_ : RelationalType) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_TYPE, ClassMetamodel.DATATYPE, List(type_)) match {
                    case Some(datatype) => Some(new AttributeToType(attribute, datatype.asInstanceOf[ClassDatatype]))
                    case _ => None
                }
            // the type is described by a table
            case Some(table: RelationalTable) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_CLASS, ClassMetamodel.CLASS, List(table)) match {
                    case Some(class_) => Some(new AttributeToType(attribute, class_.asInstanceOf[ClassClass]))
                    case _ => None
                }
            // otherwise
            case _ => None
        }
    }


    def makeSVAttributeToOwner(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                               column: RelationalColumn, attribute: ClassAttribute): Option[DynamicLink] =
        RelationalMetamodel.getColumnOwner(column, model) match {
            case Some(owner) =>
                Resolve.resolve(tls, model, dynamic_mm, PATTERN_CLASS, ClassMetamodel.CLASS, List(owner)) match {
                    case Some(class_) =>
                        Some(new AttributeToClass(attribute, class_.asInstanceOf[ClassClass]))
                    case _ => None
                }
            case _ => None
        }

    def makeMVAttributeToOwner(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                               owner_table: RelationalTable, attribute: ClassAttribute): Option[AttributeToClass] =
        Resolve.resolve(tls, model, dynamic_mm, PATTERN_CLASS, ClassMetamodel.CLASS, List(owner_table)) match {
            case Some(class_) => Some(new AttributeToClass(attribute, class_.asInstanceOf[ClassClass]))
            case _ => None
        }

    def makeMVAttributeToType(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                              table: RelationalTable, attribute: ClassAttribute): Option[DynamicLink] =
      RelationalMetamodel.getMVTableType(table, model) match {
          case Some(type_ : RelationalType) =>
              Resolve.resolve(tls, model, dynamic_mm, PATTERN_TYPE, ClassMetamodel.DATATYPE, List(type_)) match {
                  case Some(datatype) => Some(new AttributeToType(attribute, datatype.asInstanceOf[ClassDatatype]))
                  case _ => None
              }
          case Some(table: RelationalTable) =>
              Resolve.resolve(tls, model, dynamic_mm, PATTERN_CLASS, ClassMetamodel.CLASS, List(table)) match {
                  case Some(class_) => Some(new AttributeToType(attribute, class_.asInstanceOf[ClassClass]))
                  case _ => None
              }
          case _ => None
      }

    def relational2class(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "Type2Datatype",
                    types = List(RelationalMetamodel.TYPE),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_TYPE,
                            elementExpr =
                              (_, _, l) => if (l.isEmpty) None else {
                                    val type_ = l.head.asInstanceOf[RelationalType]
                                    Some(new ClassDatatype(type_.getId, type_.getName))
                              }
                        )
                    )
                ), // Table2Datatype
                new RuleImpl(
                    name = "Table2Class",
                    types = List(RelationalMetamodel.TABLE),
                    from = (_, l) => Some(l.head.asInstanceOf[RelationalTable].getName.indexOf("_") == -1),
                    to = List(
                        new OutputPatternElementImpl(
                            name = PATTERN_CLASS,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val table = l.head.asInstanceOf[RelationalTable]
                                Some(new ClassClass(table.getId, table.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, t, c) => {
                                        makeClassToAttributes_SV_MV(tls, sm.asInstanceOf[RelationalModel],
                                            t.head.asInstanceOf[RelationalTable],
                                            c.asInstanceOf[ClassClass])
                                    }
                                )
                            )
                        )
                    )
                ), // Table2Class
                new RuleImpl(
                    name = "Column2Attribute",
                    types = List(RelationalMetamodel.COLUMN),
                    from = (sm, l) =>
                        Some(RelationalMetamodel.isNotKeyOf(l.head.asInstanceOf[RelationalColumn], sm.asInstanceOf[RelationalModel])),
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_SVATTRIBUTE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val column = l.head.asInstanceOf[RelationalColumn]
                                Some(new ClassAttribute(column.getId, column.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) =>
                                        makeSVAttributeToType(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                ),

                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) =>
                                        makeSVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                )
                            )
                        )
                    )
                ), // Column2Attribute
                new RuleImpl(
                    name = "Multivalued",
                    types = List(RelationalMetamodel.TABLE, RelationalMetamodel.TABLE),
                    from = (_, l) => {
                        val t1 = l.head.asInstanceOf[RelationalTable].getName
                        val t2 = l(1).asInstanceOf[RelationalTable].getName
                        Some(t2.indexOf("_") != -1 & !t2.equals(t1) & t2.startsWith(t1))
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MVATTRIBUTE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val t2 = l(1).asInstanceOf[RelationalTable]
                                Some(new ClassAttribute(
                                    t2.getId.replace("pivot", ""),
                                    t2.getName.substring(t2.getName.indexOf("_") + 1, t2.getName.length),
                                    true))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        val owner_table = ts.head.asInstanceOf[RelationalTable]
                                        makeMVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel], owner_table,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        val table = ts(1).asInstanceOf[RelationalTable]
                                        makeMVAttributeToType(tls, sm.asInstanceOf[RelationalModel], table,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                )
                            )
                        )
                    )
                ) // Multivalued
            )
        )
    }
}
