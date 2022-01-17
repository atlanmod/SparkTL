package org.atlanmod.class2relational.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.class2relational.model.classmodel.ClassModel
import org.atlanmod.class2relational.model.classmodel.element.{ClassAttribute, ClassClass, ClassDatatype}
import org.atlanmod.class2relational.model.classmodel.metamodel.ClassMetamodel
import org.atlanmod.class2relational.model.relationalmodel.element.{RelationalColumn, RelationalTable, RelationalType}
import org.atlanmod.class2relational.model.relationalmodel.link.{ColumnToType, TableToColumns, TableToKeys}
import org.atlanmod.class2relational.model.relationalmodel.metamodel.{RelationalMetamodel, RelationalMetamodelNaive}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

import scala.util.Random

object Class2Relational {

    val random: Random.type = scala.util.Random

    final val PATTERN_TABLE : String = "tab"
    final val PATTERN_SVCOLUMNS : String = "svcol"
    final val PATTERN_TYPE : String = "type"
    final val PATTERN_KEY: String = "key"
    final val PATTERN_PIVOT: String = "pivot"
    final val PATTERN_PIVOT_SOURCE: String = "psrc"
    final val PATTERN_PIVOT_TARGET: String = "ptrg"

    def class2relational(class_metamodel: ClassMetamodel, relational_metamodel: RelationalMetamodel,
                         sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl(List(
            new RuleImpl(name = "Datatype2Type",
                types = List(class_metamodel.DATATYPE),
                from = (_, _) => { my_sleep(sleeping_guard, random.nextInt()); Some(true) },
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_TYPE,
                        elementExpr = (_, _, pattern) => {
                            my_sleep(sleeping_instantiate, random.nextInt())
                            val datatype = pattern.head.asInstanceOf[ClassDatatype]
                            Some(new RelationalType(datatype.getId, datatype.getName))
                        })
                )),
            new RuleImpl(name = "Class2Table",
                types = List(class_metamodel.CLASS),
                from = (_, _) => {my_sleep(sleeping_guard, random.nextInt()); Some(true)},
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_TABLE,
                        elementExpr = (_, _, pattern) => {
                            my_sleep(sleeping_instantiate, random.nextInt())
                            val class_ = pattern.head.asInstanceOf[ClassClass]
                            Some(new RelationalTable(class_.getId, class_.getName))
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    val class_ = pattern.head.asInstanceOf[ClassClass]
                                    val model = sm.asInstanceOf[ClassModel]
                                    val table = output.asInstanceOf[RelationalTable]
                                    Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_KEY, relational_metamodel.COLUMN, List(class_)) match {
                                        case Some(k: RelationalColumn) => Some(new TableToKeys(table, k))
                                        case _ => None
                                    }
                                }
                            ),
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => {
                                    my_sleep(sleeping_apply, random.nextInt())
                                    val class_ = pattern.head.asInstanceOf[ClassClass]
                                    val model = sm.asInstanceOf[ClassModel]
                                    val table = output.asInstanceOf[RelationalTable]
                                    val key = Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_KEY, RelationalMetamodelNaive.COLUMN, List(class_))
                                    val cols = class_metamodel.getClassAttributes(class_, model).map(
                                        attributes => Resolve.resolveAll(tls, model, class_metamodel.metamodel, PATTERN_SVCOLUMNS, relational_metamodel.COLUMN,
                                            ListUtils.singletons(attributes)).getOrElse(List()))
                                    (key, cols) match {
                                        case (Some(k: RelationalColumn), Some(lcols: List[RelationalColumn])) =>
                                                Some(new TableToColumns(table, k:: lcols))
                                        case _ => None
                                    }
                                }
                            )
                        )
                    ),
                    new OutputPatternElementImpl(name = PATTERN_KEY,
                        elementExpr = (_, _, pattern) => {
                            if (pattern.isEmpty) None else { my_sleep(sleeping_instantiate, random.nextInt())
                                val class_ = pattern.head.asInstanceOf[ClassClass]
                                Some(new RelationalColumn(class_.getId + "Id", "Id"))
                            }}
                    )
                )
            ),
            new RuleImpl(name = "SVAttribute2Column",
                types = List(class_metamodel.ATTRIBUTE),
                from = (_, pattern) => { my_sleep(sleeping_guard, random.nextInt()); Some(!pattern.head.asInstanceOf[ClassAttribute].isMultivalued)},
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_SVCOLUMNS,
                        elementExpr = (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            val attribute = pattern.head.asInstanceOf[ClassAttribute]
                            Some(new RelationalColumn(attribute.getId, attribute.getName))
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl((tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                val attribute = pattern.head.asInstanceOf[ClassAttribute]
                                val column = output.asInstanceOf[RelationalColumn]
                                val model = sm.asInstanceOf[ClassModel]
                                class_metamodel.getAttributeType(attribute, model) match {
                                    case Some(dt : ClassDatatype) =>
                                        Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_TYPE, relational_metamodel.TYPE, List(dt)) match {
                                            case Some(type_ : RelationalType) => Some(new ColumnToType(column, type_))
                                            case _ => None
                                        }
                                    case Some(cl: ClassClass) =>
                                        Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_TABLE, relational_metamodel.TABLE, List(cl)) match {
                                            case Some(table: RelationalTable) => Some(new ColumnToType(column, table))
                                            case _ => None
                                        }
                                    case _ => None
                                }
                            })
                        )
                    )
                )
            ),
            new RuleImpl(name = "MVAttribute2Column",
                types = List(class_metamodel.ATTRIBUTE),
                from = (_, pattern) => { my_sleep(sleeping_guard, random.nextInt()); Some(pattern.head.asInstanceOf[ClassAttribute].isMultivalued)},
                to = List(
                    new OutputPatternElementImpl(name = PATTERN_PIVOT,
                        elementExpr = (_, sm, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            val attribute = pattern.head.asInstanceOf[ClassAttribute]
                            val model = sm.asInstanceOf[ClassModel]
                            class_metamodel.getAttributeOwner(attribute, model) match {
                                case Some(owner) =>
                                    Some(new RelationalTable(attribute.getId, owner.getName + "_" + attribute.getName))
                                case _ => None
                            }
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl((tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                val attribute = pattern.head.asInstanceOf[ClassAttribute]
                                val model = sm.asInstanceOf[ClassModel]
                                val table = output.asInstanceOf[RelationalTable]
                                val psrc = Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_PIVOT_SOURCE, RelationalMetamodelNaive.COLUMN, List(attribute))
                                val ptrg = Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_PIVOT_TARGET, RelationalMetamodelNaive.COLUMN, List(attribute))
                                (psrc, ptrg) match {
                                    case (Some(c1: RelationalColumn), Some(c2: RelationalColumn)) =>
                                        Some(new TableToColumns(table, List(c1, c2)))
                                    case _ => None
                                }
                            }),
                            new OutputPatternElementReferenceImpl((tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                val attribute = pattern.head.asInstanceOf[ClassAttribute]
                                val model = sm.asInstanceOf[ClassModel]
                                val table = output.asInstanceOf[RelationalTable]
                                val psrc = Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_PIVOT_SOURCE, RelationalMetamodelNaive.COLUMN, List(attribute))
                                val ptrg = Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_PIVOT_TARGET, RelationalMetamodelNaive.COLUMN, List(attribute))
                                (psrc, ptrg) match {
                                    case (Some(c1: RelationalColumn), Some(c2: RelationalColumn)) =>
                                        Some(new TableToKeys(table, List(c1, c2)))
                                    case _ => None
                                }
                            })
                        )),
                    new OutputPatternElementImpl(name = PATTERN_PIVOT_SOURCE,
                        elementExpr = (_, _, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            Some(new RelationalColumn(pattern.head.asInstanceOf[ClassAttribute].getId + "psrc", "Id"))
                        }),
                    new OutputPatternElementImpl(name = PATTERN_PIVOT_TARGET,
                        elementExpr = (_, sm, pattern) => { my_sleep(sleeping_instantiate, random.nextInt())
                            val attribute = pattern.head.asInstanceOf[ClassAttribute]
                            class_metamodel.getAttributeType(attribute, sm.asInstanceOf[ClassModel]) match {
                                case Some(type_) =>
                                    Some(new RelationalColumn(attribute.getId + "ptrg", type_.getName))
                                case _ => None
                            }
                        },
                        outputElemRefs = List(
                            new OutputPatternElementReferenceImpl(
                                (tls, _, sm, pattern, output) => { my_sleep(sleeping_apply, random.nextInt())
                                    val model = sm.asInstanceOf[ClassModel]
                                    val attribute = pattern.head.asInstanceOf[ClassAttribute]
                                    val column = output.asInstanceOf[RelationalColumn]
                                    class_metamodel.getAttributeType(attribute, model) match {
                                        case Some(dt : ClassDatatype) =>
                                            Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_TYPE, relational_metamodel.TYPE, List(dt)) match {
                                                case Some(type_ : RelationalType) => Some(new ColumnToType(column, type_))
                                                case _ => None
                                            }
                                        case Some(cl: ClassClass) =>
                                            Resolve.resolve(tls, model, class_metamodel.metamodel, PATTERN_TABLE, relational_metamodel.TABLE, List(cl)) match {
                                                case Some(table: RelationalTable) => Some(new ColumnToType(column, table))
                                                case _ => None
                                            }
                                        case _ => None
                                    }
                            })
                        ))
                )
            )
        ))
    }
}