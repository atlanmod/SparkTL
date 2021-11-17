package org.atlanmod.class2relational.transformation

object Class2RelationalSimple {

//    final val mm =  ClassMetamodel.metamodel
//
//    val random = scala.util.Random
//
//    final val PATTERN_TABLE : String = "tab"
//    final val PATTERN_COLUMN : String = "col"
//
//    def class2relational(sleeping_guard: Int = 0, sleeping_instantiate: Int = 0, sleeping_apply: Int = 0)
//    :Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
//
//        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
//            List(
//                new RuleImpl(
//                    name = "Class2Table",
//                    types = List(ClassMetamodel.CLASS),
//                    from = (m, l) => {
//                        my_sleep(sleeping_guard, random.nextInt())
//                        Some(true)
//                    },
//                    to = List(
//                        new OutputPatternElementImpl(
//                            name = PATTERN_TABLE,
//                            elementExpr =
//                              (_, _, l) =>
//                                  if (l.isEmpty) None
//                                  else {
//                                      my_sleep(sleeping_instantiate, random.nextInt())
//                                      val class_ = l.head.asInstanceOf[ClassClass]
//                                      Some(new RelationalTable(class_.getId,class_.getName))
//                                  },
//                            outputElemRefs = List(
//                                new OutputPatternElementReferenceImpl(
//                                    (tls, _, sm, c, t) => {
//                                        my_sleep(sleeping_apply, random.nextInt())
//                                        val class_ = c.head.asInstanceOf[ClassClass]
//                                        ClassMetamodel.getClassAttributes(class_, sm.asInstanceOf[ClassModel]) match {
//                                            case Some(attrs) =>
//                                                val cols = Resolve.resolveAll(tls, sm, mm, PATTERN_COLUMN,
//                                                    RelationalMetamodel.COLUMN , ListUtils.singletons(attrs))
//                                                cols match {
//                                                    case Some(columns) if columns.nonEmpty =>
//                                                        Some(new TableToColumns(
//                                                            t.asInstanceOf[RelationalTable],
//                                                            columns.asInstanceOf[List[RelationalColumn]]
//                                                        ))
//                                                    case _ => None
//                                                }
//                                            case _ => None
//                                        }
//                                    }
//                                )
//                            )
//                        )
//                    )
//                ),
//                new RuleImpl(
//                    name = "Attribute2Column",
//                    types = List(ClassMetamodel.ATTRIBUTE),
//                    from = (m, l) => {
//                        my_sleep(sleeping_guard, random.nextInt())
//                        Some(true)
//                    },
//                    to = List(
//                        new OutputPatternElementImpl(
//                            name = PATTERN_COLUMN,
//                            elementExpr =
//                              (_, _, l) =>
//                                  if (l.isEmpty) None else {
//                                      my_sleep(sleeping_instantiate, random.nextInt())
//                                      val att = l.head.asInstanceOf[ClassAttribute]
//                                      Some(new RelationalColumn(att.getId, att.getName))
//                                  },
//                            outputElemRefs = List(
//                                new OutputPatternElementReferenceImpl(
//                                    (tls, _, sm, a, c) => {
//                                        my_sleep(sleeping_apply, random.nextInt())
//                                        val attribute = a.head.asInstanceOf[ClassAttribute]
//                                        ClassMetamodel.getAttributeOwner(attribute, sm.asInstanceOf[ClassModel]) match {
//                                            case Some(class_) =>
//                                                Resolve.resolve(tls, sm, mm, PATTERN_TABLE, RelationalMetamodel.TABLE,
//                                                    List(class_)) match {
//                                                    case Some(table) =>
//                                                        Some(new ColumnToTable(
//                                                            c.asInstanceOf[RelationalColumn],
//                                                            table.asInstanceOf[RelationalTable])
//                                                        )
//                                                    case _ => None
//                                                }
//                                            case _ => None
//                                        }
//                                    }
//                                )
//                            )
//                        )
//                    )
//                )
//            ))
//    }

}