package org.atlanmod.class2relational.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassDatatype}
import org.atlanmod.class2relational.model.classmodel.link.{AttributeToType, ClassToAttributes}
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodel
import org.atlanmod.class2relational.model.relationalmodel.RelationalModel
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalType}
import org.atlanmod.class2relational.model.relationalmodel.metamodel.RelationalMetamodel
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

import scala.util.Random

object Relational2Class {
    val random: Random.type = scala.util.Random

    final val PATTERN_TYPE : String = "type"
    final val PATTERN_CLASS : String = "class"
    final val PATTERN_SVATTRIBUTE : String = "svatt"
    final val PATTERN_MVATTRIBUTE : String = "mvatt"
    final val PATTERN_MVATTRIBUTE_TYPECLASS : String = "mvatt_tc"
    final val PATTERN_MVATTRIBUTE_DATATYPE : String = "mvatt_dt"

    def relational2class(relational_metamodel: RelationalMetamodel, class_metamodel: ClassMetamodel,
                         isPivot: (RelationalTable, RelationalModel, RelationalMetamodel) => Boolean = RelationalHelper.isPivot_default,
                         isNotPivot: (RelationalTable, RelationalModel, RelationalMetamodel) => Boolean = RelationalHelper.isNotPivot_default,
                         sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl(List(
            new RuleImpl(name="Type2DataType",
                types = List(relational_metamodel.TYPE),
                from = (_, _) => { my_sleep(sleeping_guard, random.nextInt()); Some(true) },
                to = List( new OutputPatternElementImpl(
                    name = PATTERN_TYPE,
                    elementExpr =
                      (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                          val type_ = pattern.head.asInstanceOf[RelationalType]
                          Some(new ClassDatatype(type_.getId, type_.getName))
                      }
                ))
            ),
            new RuleImpl(name="Table2Class",
                types = List(relational_metamodel.TABLE),
                from = (sm, pattern) => { my_sleep(sleeping_guard, random.nextInt())
                    val model = sm.asInstanceOf[RelationalModel]
                    val table = pattern.head.asInstanceOf[RelationalTable]
                    Some(isNotPivot(table, model, relational_metamodel))},
                to = List(new OutputPatternElementImpl(name = PATTERN_CLASS,
                    elementExpr = (_, _, pattern) => {
                        val table = pattern.head.asInstanceOf[RelationalTable]
                        Some(new ClassClass(table.getId, table.getName))
                    },
                    outputElemRefs = List(
                        new OutputPatternElementReferenceImpl((tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                            val model = sm.asInstanceOf[RelationalModel]
                            val table = pattern.head.asInstanceOf[RelationalTable]
                            val class_ = output.asInstanceOf[ClassClass]
                            val sv_attributes: List[ClassAttribute] =
                                relational_metamodel.getTableColumns(table, model) match {
                                    case Some(cols: List[RelationalColumn]) =>
                                        Resolve.resolveAll(tls, model, relational_metamodel.metamodel, PATTERN_SVATTRIBUTE, class_metamodel.ATTRIBUTE,
                                            ListUtils.singletons(cols)) match {
                                            case Some(attributes: List[ClassAttribute]) => attributes
                                            case _ => List()
                                        }
                                    case _ => List()
                                }
                            val mv_attributes: List[ClassAttribute] = {
                                val mv_tables = relational_metamodel.getAllTable(model).filter(t => isPivot(t, model, relational_metamodel)
                                  && t.getName.startsWith(table.getName + "_"))
                                Resolve.resolveAll(tls, model, relational_metamodel.metamodel, PATTERN_MVATTRIBUTE, class_metamodel.ATTRIBUTE, ListUtils.singletons(mv_tables)) match {
                                    case Some(attributes: List[ClassAttribute]) => attributes
                                    case _ => List()
                                }
                            }
                            sv_attributes ++ mv_attributes match {
                                case h::t => Some(new ClassToAttributes(class_, h::t))
                                case List() => None
                            }
                        })
                    )
                ))
            ),
            new RuleImpl(name="Column2Attribute",
                types = List(relational_metamodel.COLUMN),
                from = (sm, pattern) => { my_sleep(sleeping_guard, random.nextInt())
                    val column = pattern.head.asInstanceOf[RelationalColumn]
                    val model = sm.asInstanceOf[RelationalModel]
                    Some(relational_metamodel.getKeyOf(column, model).isEmpty) },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_SVATTRIBUTE,
                        elementExpr = (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            val column = pattern.head.asInstanceOf[RelationalColumn]
                            Some(new ClassAttribute(column.getId, column.getName))
                        },
                        outputElemRefs = List( new OutputPatternElementReferenceImpl(
                            (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                val model = sm.asInstanceOf[RelationalModel]
                                val column = pattern.head.asInstanceOf[RelationalColumn]
                                val attribute = output.asInstanceOf[ClassAttribute]
                                relational_metamodel.getColumnType(column, model) match {
                                    case Some(type_ : RelationalType) =>
                                        Resolve.resolve(tls, model, relational_metamodel.metamodel, PATTERN_TYPE, class_metamodel.DATATYPE, List(type_)) match {
                                            case Some(datatype: ClassDatatype) => Some(new AttributeToType(attribute, datatype))
                                            case _ => None
                                        }
                                    case Some(table: RelationalTable) =>
                                        Resolve.resolve(tls, model, relational_metamodel.metamodel, PATTERN_CLASS, class_metamodel.CLASS, List(table)) match {
                                            case Some(datatype: ClassClass) => Some(new AttributeToType(attribute, datatype))
                                            case _ => None
                                        }
                                    case _ => None
                                }
                            }
                        ))
                    )
                )
            ),
            new RuleImpl(name="Multivalued2Attribute",
                types = List(relational_metamodel.TABLE),
                from = (sm, pattern) => { my_sleep(sleeping_guard, random.nextInt())
                    val model = sm.asInstanceOf[RelationalModel]
                    val table = pattern.head.asInstanceOf[RelationalTable]
                    Some(isPivot(table, model, relational_metamodel))},
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_MVATTRIBUTE,
                        elementExpr = (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            val table = pattern.head.asInstanceOf[RelationalTable]
                            Some(new ClassAttribute(table.getId, table.getName.substring(table.getName.indexOf("_") + 1, table.getName.length), true))
                        },
                        outputElemRefs = List(new OutputPatternElementReferenceImpl(
                            (tls, _, sm, pattern, output) => {
                                val table = pattern.head.asInstanceOf[RelationalTable]
                                val model = sm.asInstanceOf[RelationalModel]
                                val attribute = output.asInstanceOf[ClassAttribute]
                                relational_metamodel.getTableKeys(table, model) match {
                                    case Some(keys: List[RelationalColumn]) =>
                                        keys.find(p => p.getId.contains("ptrg")) match {
                                            case Some(key: RelationalColumn) =>
                                                relational_metamodel.getColumnType(key, model) match {
                                                    case Some(type_ : RelationalType) =>
                                                        Resolve.resolve(tls, model, relational_metamodel.metamodel, PATTERN_TYPE, class_metamodel.DATATYPE, List(type_)) match {
                                                            case Some(datatype: ClassDatatype) => Some(new AttributeToType(attribute, datatype))
                                                            case _ => None
                                                        }
                                                    case Some(table: RelationalTable) =>
                                                        Resolve.resolve(tls, model, relational_metamodel.metamodel, PATTERN_CLASS, class_metamodel.CLASS, List(table)) match {
                                                            case Some(datatype: ClassClass) => Some(new AttributeToType(attribute, datatype))
                                                            case _ => None
                                                        }
                                                    case _ => None
                                                }
                                            case _ => None
                                        }
                                    case _ => None
                                }
                            }
                        ))
                    )
                )
            ),
        ))
    }
}
