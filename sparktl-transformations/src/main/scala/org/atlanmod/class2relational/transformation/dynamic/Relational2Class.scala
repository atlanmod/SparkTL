package org.atlanmod.class2relational.transformation.dynamic

import org.atlanmod.class2relational.model.classmodel._
import org.atlanmod.class2relational.model.relationalmodel._
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{TraceLinks, Transformation}
import org.atlanmod.tl.util.ListUtils

object Relational2Class {


    final val PATTERN_TYPE : String = "type"
    final val PATTERN_CLASS : String = "class"
    final val PATTERN_SVATTRIBUTE : String = "svatt"
    final val PATTERN_MVATTRIBUTE : String = "mvatt"
    final val PATTERN_MVATTRIBUTE_TYPECLASS : String = "mvatt_tc"
    final val PATTERN_MVATTRIBUTE_DATATYPE : String = "mvatt_dt"

    final val TIME_SLEEP : Int = 10

    val random = scala.util.Random

    val dynamic_mm =  new DynamicMetamodel[DynamicElement, DynamicLink]()

    def makeClassToAttributes_SV(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                                 table: RelationalTable, class_ : ClassClass): List[ClassAttribute] = {
        var sv_attributes: List[ClassAttribute] = List()
        RelationalMetamodel.getSVColumnsOfTable(table, model) match {
            case Some(columns) =>
                Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_SVATTRIBUTE,
                    ClassMetamodel.ATTRIBUTE, ListUtils.singletons(columns)) match {
                    case Some(attributes: List[ClassAttribute]) =>
                        sv_attributes = sv_attributes ++ attributes
                    case _ =>
                }
            case _ =>
        }
        sv_attributes
    }

    def makeClassToAttributes_SV_MV(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                                    table: RelationalTable, class_ : ClassClass): Option[ClassToAttributes] = {
        var sv_mv_attributes: List[ClassAttribute] = List()

        RelationalMetamodel.getSVColumnsOfTable(table, model) match {
            case Some(columns) =>
                Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_SVATTRIBUTE,
                    ClassMetamodel.ATTRIBUTE, ListUtils.singletons(columns)) match {
                    case Some(attributes: List[ClassAttribute]) =>
                        sv_mv_attributes = sv_mv_attributes ++ attributes
                    case _ =>
                }
            case _ =>
        }

        RelationalMetamodel.getMVTablesOfTable(table, model) match {
            case Some(tables) =>
                val sps = tls.filter(tl => tables.contains(tl.getSourcePattern.head)).getSourcePatterns
                (
                  Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_MVATTRIBUTE_DATATYPE, ClassMetamodel.ATTRIBUTE, sps),
                  Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_MVATTRIBUTE_TYPECLASS, ClassMetamodel.ATTRIBUTE, sps),
                ) match {
                    case (Some(attributes_dt: List[ClassAttribute]), Some(attributes_tc: List[ClassAttribute])) =>
                        sv_mv_attributes = sv_mv_attributes ++ attributes_dt ++ attributes_tc
                    case (None, Some(attributes: List[ClassAttribute])) =>
                        sv_mv_attributes = sv_mv_attributes ++ attributes
                    case (Some(attributes: List[ClassAttribute]), None) =>
                        sv_mv_attributes = sv_mv_attributes ++ attributes
                    case _ =>
                }
        }

        // TODO : check this function, to don't use getMVTablesOfTableOld anymore
        RelationalMetamodel.getMVTablesOfTableOld(table, model) match {
            case Some(tables) =>
                Resolve.resolveAll(tls, model, dynamic_mm, PATTERN_MVATTRIBUTE,
                    ClassMetamodel.ATTRIBUTE, tables) match {
                    case Some(attributes: List[ClassAttribute]) =>
                        sv_mv_attributes = sv_mv_attributes ++ attributes
                }
        }

        Some(new ClassToAttributes(class_, sv_mv_attributes))
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
                              ttype: RelationalType, attribute: ClassAttribute): Option[AttributeToType] =
      Resolve.resolve(tls, model, dynamic_mm, PATTERN_TYPE, ClassMetamodel.DATATYPE, List(ttype)) match {
          case Some(datatype) => Some(new AttributeToType(attribute, datatype.asInstanceOf[ClassDatatype]))
          case _ => None
      }

    def makeMVAttributeToType(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
                              table: RelationalTable, attribute: ClassAttribute): Option[AttributeToType] =
        Resolve.resolve(tls, model, dynamic_mm, PATTERN_CLASS, ClassMetamodel.CLASS, List(table)) match {
            case Some(class_) => Some(new AttributeToType(attribute, class_.asInstanceOf[ClassDatatype]))
            case _ => None
        }
    
    def makeMVAttributeToTypeFromGotTable(tls: TraceLinks[DynamicElement, DynamicElement], model: RelationalModel,
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

    def my_sleep(millis: Int, rd: Int): Int = {
        var d = rd.toDouble
        val end = System.nanoTime() + millis * 1e6
        var current = System.nanoTime
        while(current < end){
            current = System.nanoTime
            d += 1.0
        }
        d.toInt
    }

    def isPivot(m:RelationalModel, t1:RelationalTable, t2:RelationalTable) : Boolean = {
        val n1 = t1.getName
        val n2 = t2.getName
        n2.indexOf("_") != -1 & !n2.equals(n1) & n2.startsWith(n1)
    }

    def isNotPivot(m:RelationalModel, t: RelationalTable): Boolean = {
        t.getName.indexOf("_") == -1
    }

    def isPivot_n2(model:RelationalModel, town:RelationalTable, tattr:RelationalTable) : Boolean = {
        /*
            Goal: find cref, ttype, and cid such as
            ```
            tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) &
              RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
              RelationalMetamodel.isKeyOf(cref, tattr, model) &
              cref.getName.equals(ttype.getName) &
              RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
              RelationalMetamodel.isKeyOf(cid, tattr, model) &
              cid.getName.equals("Id")
             ```
             is respected
             */

        val columns: List[RelationalColumn] = RelationalMetamodel.getAllColumns(model)
        val typables: List[RelationalTypable] = RelationalMetamodel.getAllTypable(model)

        var g1 = false
        for (cref <- columns){
            // Find cref
            val c1 = RelationalMetamodel.getColumnOwner(cref, model).contains(tattr)
            val c2 = RelationalMetamodel.isKeyOf(cref, tattr, model)
            var g2 = false
            for (ttype <- typables) {
                // Find ttype
                val c3 = cref.getName.equals(ttype.getName)
                g2 = g2 || c3
            }
            g1 = g1 || (c1 && c2 && g2)
        }

        var g3 = false
        for (cid <- columns){
            // Find cid
            val c1 = RelationalMetamodel.getColumnOwner(cid, model).contains(tattr)
            val c2 = RelationalMetamodel.isKeyOf(cid, tattr, model)
            val c3 = cid.getName.equals("Id")
            g3 = g3 || (c1 && c2 && c3)
        }

        tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) && g1 && g3

    }

    def isNotPivot_n2(model:RelationalModel, t:RelationalTable) : Boolean = {
        /*
            Goal: find cref, ttype, and cid such as
            ```
            tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) &
              RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
              RelationalMetamodel.isKeyOf(cref, tattr, model) &
              cref.getName.equals(ttype.getName) &
              RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
              RelationalMetamodel.isKeyOf(cid, tattr, model) &
              cid.getName.equals("Id")
             ```
             is respected
             */
        println("N2")
        val columns: List[RelationalColumn] = RelationalMetamodel.getAllColumns(model)
        val typables: List[RelationalTypable] = RelationalMetamodel.getAllTypable(model)

        var g1 = false
        for (cref <- columns){
            // Find cref
            val c1 = RelationalMetamodel.getColumnOwner(cref, model).contains(t)
            val c2 = RelationalMetamodel.isKeyOf(cref, t, model)
            var g2 = false
            for (ttype <- typables) {
                // Find ttype
                val c3 = cref.getName.equals(ttype.getName)
                g2 = g2 || c3
            }
            g1 = g1 || (c1 && c2 && g2)
        }

        var g3 = false
        for (cid <- columns){
            // Find cid
            val c1 = RelationalMetamodel.getColumnOwner(cid, model).contains(t)
            val c2 = RelationalMetamodel.isKeyOf(cid, t, model)
            val c3 = cid.getName.equals("Id")
            g3 = g3 || (c1 && c2 && c3)
        }

        t.getName.indexOf('_') == -1

    }

    def isNotPivot_complex(model:RelationalModel, t:RelationalTable) : Boolean ={

        println("complex")
        val columns: List[RelationalColumn] = RelationalMetamodel.getAllColumns(model)
        val typables: List[RelationalTypable] = RelationalMetamodel.getAllTypable(model)
        val tables: List[RelationalTypable] = RelationalMetamodel.getAllTable(model)

        var result = true
        for (town <- tables) {
            for (cref <- columns) {
                for (ttype <- typables) {
                    for (cid <- columns) {
                        if (t.getName.indexOf("_") != -1 & town != t & t.getName.startsWith(town.getName) &
                          RelationalMetamodel.getColumnOwner(cref, model).contains(t) &
                          RelationalMetamodel.isKeyOf(cref, t, model) &
                          cref.getName.equals(ttype.getName) &
                          RelationalMetamodel.getColumnOwner(cid, model).contains(t) &
                          RelationalMetamodel.isKeyOf(cid, t, model) &
                          cid.getName.equals("Id"))
                            result = true
                    }
                }
            }
        }
        result
    }
    def isPivot_complex(model:RelationalModel, town:RelationalTable, tattr:RelationalTable) : Boolean = {
        /*
        Goal: find cref, ttype, and cid such as
        ```
        tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) &
          RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
          RelationalMetamodel.isKeyOf(cref, tattr, model) &
          cref.getName.equals(ttype.getName) &
          RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
          RelationalMetamodel.isKeyOf(cid, tattr, model) &
          cid.getName.equals("Id")
         ```
         is respected
         */
        println("complex")
        val columns: List[RelationalColumn] = RelationalMetamodel.getAllColumns(model)
        val typables: List[RelationalTypable] = RelationalMetamodel.getAllTypable(model)

        var result = false
        for (cref <- columns){
            for (ttype <- typables) {
                for (cid <- columns){
                    if (tattr.getName.indexOf("_") != -1 & town != tattr & tattr.getName.startsWith(town.getName) &
                    RelationalMetamodel.getColumnOwner(cref, model).contains(tattr) &
                    RelationalMetamodel.isKeyOf(cref, tattr, model) &
                    cref.getName.equals(ttype.getName) &
                    RelationalMetamodel.getColumnOwner(cid, model).contains(tattr) &
                    RelationalMetamodel.isKeyOf(cid, tattr, model) &
                    cid.getName.equals("Id"))
                        result = true
                }
            }
        }
        result
    }

    def relational2class(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0,
                         foo_pivot: (RelationalModel, RelationalTable, RelationalTable) => Boolean = isPivot,
                         foo_notpivot: (RelationalModel, RelationalTable) => Boolean = isNotPivot)
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
                        new OutputPatternElementImpl(
                            name = PATTERN_TYPE,
                            elementExpr =
                              (_, _, l) => if (l.isEmpty) None else {
                                  val type_ = l.head.asInstanceOf[RelationalType]
                                  my_sleep(sleeping_instantiate, random.nextInt())
                                  Some(new ClassDatatype(type_.getId, type_.getName))
                              }
                        )
                    )
                ), // Type2Datatype
                new RuleImpl(
                    name = "Table2Class",
                    types = List(RelationalMetamodel.TABLE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        Some(foo_notpivot(m.asInstanceOf[RelationalModel], l.head.asInstanceOf[RelationalTable]))
                    },
                    to = List(
                        new OutputPatternElementImpl(
                            name = PATTERN_CLASS,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val table = l.head.asInstanceOf[RelationalTable]
                                my_sleep(sleeping_instantiate, random.nextInt())
                                Some(new ClassClass(table.getId, table.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, t, c) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeClassToAttributes_SV_MV(tls, sm.asInstanceOf[RelationalModel],
                                            t.head.asInstanceOf[RelationalTable], c.asInstanceOf[ClassClass])
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
                        {
                            my_sleep(sleeping_guard, random.nextInt())
                            Some(
                                RelationalMetamodel.isNotAKey(l.head.asInstanceOf[RelationalColumn], sm.asInstanceOf[RelationalModel])
                            )
                        },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_SVATTRIBUTE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val column = l.head.asInstanceOf[RelationalColumn]
                                my_sleep(sleeping_instantiate, random.nextInt())
                                Some(new ClassAttribute(column.getId, column.getName, false))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) =>{
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeSVAttributeToType(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                    }
                                ), // Attribute to type
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, a) =>{
                                        my_sleep(sleeping_apply, random.nextInt())
                                        makeSVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel],
                                            c.head.asInstanceOf[RelationalColumn], a.asInstanceOf[ClassAttribute])
                                    }
                                ) // Attribute to owner
                            )
                        )
                    )
                ), // Column2Attribute
                new RuleImpl(
                    name = "Multivalued",
                    types = List(RelationalMetamodel.TABLE, RelationalMetamodel.TABLE),
                    from = (m, l) => {
                        my_sleep(sleeping_guard, random.nextInt())
                        val t1 = l.head.asInstanceOf[RelationalTable]
                        val t2 = l(1).asInstanceOf[RelationalTable]
                        val model = m.asInstanceOf[RelationalModel]
                        Some(foo_pivot(model, t1, t2))
                    },
                    to = List(
                        new OutputPatternElementImpl(name = PATTERN_MVATTRIBUTE,
                            elementExpr = (_, _, l) => if (l.isEmpty) None else {
                                val t2 = l(1).asInstanceOf[RelationalTable]
                                my_sleep(sleeping_instantiate, random.nextInt())
                                Some(new ClassAttribute(
                                    t2.getId.replace("pivot", ""),
                                    t2.getName.substring(t2.getName.indexOf("_") + 1, t2.getName.length),
                                    true))
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val owner_table = ts.head.asInstanceOf[RelationalTable]
                                        makeMVAttributeToOwner(tls, sm.asInstanceOf[RelationalModel], owner_table,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                ),
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, ts, a) => {
                                        my_sleep(sleeping_apply, random.nextInt())
                                        val table = ts(1).asInstanceOf[RelationalTable]
                                        makeMVAttributeToTypeFromGotTable(tls, sm.asInstanceOf[RelationalModel], table,
                                            a.asInstanceOf[ClassAttribute])
                                    }
                                )
                            )
                        )
                    )
                ) // Multivalued
            ))
    }

}
