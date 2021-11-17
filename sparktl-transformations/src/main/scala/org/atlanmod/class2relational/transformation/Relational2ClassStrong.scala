package org.atlanmod.class2relational.transformation

import org.atlanmod.Utils.my_sleep
import org.atlanmod.class2relational.model.classmodel.{ClassAttribute, ClassClass, ClassDatatype}
import org.atlanmod.class2relational.model.relationalmodel._
import org.atlanmod.class2relational.transformation.Relational2Class._
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}

import scala.util.Random

object Relational2ClassStrong {

    final val PATTERN_TYPE : String = "type"
    final val PATTERN_CLASS : String = "class"
    final val PATTERN_SVATTRIBUTE : String = "svatt"
    final val PATTERN_MVATTRIBUTE : String = "mvatt"
    final val PATTERN_MVATTRIBUTE_TYPECLASS : String = "mvatt_tc"
    final val PATTERN_MVATTRIBUTE_DATATYPE : String = "mvatt_dt"

    val random: Random.type = scala.util.Random

    def relational2class(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
    : Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "Type2Datatype",
                    types = List(RelationalMetamodel.TYPE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(true)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_TYPE,
                            elementExpr =
                              (_, _, l) => if (l.isEmpty) None else {
                                  my_sleep(sleeping_instantiate, random.nextInt())
                                  val type_ = l.head.asInstanceOf[RelationalType]
                                  Some(new ClassDatatype(type_.getId, type_.getName))
                              }
                        )
                    )
                ), // Type2Datatype
                new RuleImpl(
                    name = "Table2Class",
                    types = List(RelationalMetamodel.TABLE),
                    from = (_, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(l.head.asInstanceOf[RelationalTable].getName.indexOf("_") == -1)
                    },
                    to = List(
                        new OutputPatternElementImpl(
                            name = PATTERN_CLASS,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val table = l.head.asInstanceOf[RelationalTable]
                                Some(new ClassClass(table.getId, table.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, t, c) => {
                                        my_sleep(sleeping_apply, random.nextInt())
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
                    from = (sm, l) => {
                            my_sleep(sleeping_guard, random.nextInt())
                            Some(RelationalMetamodel.isNotAKey(l.head.asInstanceOf[RelationalColumn], sm.asInstanceOf[RelationalModel]))
                        },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_SVATTRIBUTE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val column = l.head.asInstanceOf[RelationalColumn]
                                Some(new ClassAttribute(column.getId, column.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeSVAttributeToType(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeSVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                    }
                                )
                            )
                        )
                    )
                ), // Column2Attribute
                new RuleImpl(
                    name = "Multivalued_Type",
                    types = List(
                        RelationalMetamodel.TABLE, RelationalMetamodel.TYPE, RelationalMetamodel.TABLE,
                        RelationalMetamodel.COLUMN, RelationalMetamodel.COLUMN
                    ),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())

                        val model = m.asInstanceOf[RelationalModel]
                        val tattr = l.head.asInstanceOf[RelationalTable]
                        val ttype = l(1).asInstanceOf[RelationalType]
                        val town = l(2).asInstanceOf[RelationalTable]
                        val cid = l(3).asInstanceOf[RelationalColumn]
                        val cref = l(4).asInstanceOf[RelationalColumn]

                        val guard =
                            tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) &
                              RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
                              RelationalMetamodel.isKeyOf(cref, tattr, model) &
                              cref.getName.equals(ttype.getName) &
                              RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
                              RelationalMetamodel.isKeyOf(cid, tattr, model) &
                              cid.getName.equals("Id")
                        Some(guard)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MVATTRIBUTE_DATATYPE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val tattr = l.head.asInstanceOf[RelationalTable]
                                Some(new ClassAttribute(
                                    tattr.getId.replace("pivot", ""),
                                    tattr.getName.substring(tattr.getName.indexOf("_") + 1, tattr.getName.length),
                                    true))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val town = ts(2).asInstanceOf[RelationalTable]
                                        makeMVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel], town,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val ttype = ts(1).asInstanceOf[RelationalType]
                                        makeMVAttributeToType(tls, sm.asInstanceOf[RelationalModel], ttype,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                )
                            )
                        )
                    )
                ), // Multivalued of type type
                new RuleImpl(
                    name = "Multivalued_Table",
                    types = List(
                        RelationalMetamodel.TABLE, RelationalMetamodel.TABLE, RelationalMetamodel.TABLE,
                        RelationalMetamodel.COLUMN, RelationalMetamodel.COLUMN
                    ),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())

                        val model = m.asInstanceOf[RelationalModel]
                        val tattr = l.head.asInstanceOf[RelationalTable]
                        val ttype = l(1).asInstanceOf[RelationalTable]
                        val town = l(2).asInstanceOf[RelationalTable]
                        val cid = l(3).asInstanceOf[RelationalColumn]
                        val cref = l(4).asInstanceOf[RelationalColumn]
                        val guard = (tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName)) &
                          (RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
                            RelationalMetamodel.isKeyOf(cref, tattr, model) & cref.getName.equals(ttype.getName)) &
                          (RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
                            RelationalMetamodel.isKeyOf(cid, tattr, model) & cref.getName.equals("Id"))
                        Some(guard)
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MVATTRIBUTE_TYPECLASS,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                my_sleep(sleeping_instantiate, random.nextInt())
                                val tattr = l.head.asInstanceOf[RelationalTable]
                                Some(new ClassAttribute(
                                    tattr.getId.replace("pivot", ""),
                                    tattr.getName.substring(tattr.getName.indexOf("_") + 1, tattr.getName.length),
                                    true))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val town = ts(2).asInstanceOf[RelationalTable]
                                        makeMVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel], town,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val ttype = ts(1).asInstanceOf[RelationalTable]
                                        makeMVAttributeToType(tls, sm.asInstanceOf[RelationalModel], ttype,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                )
                            )
                        )
                    )
                ) // Multivalued of type table
            )
        )
    }
}
