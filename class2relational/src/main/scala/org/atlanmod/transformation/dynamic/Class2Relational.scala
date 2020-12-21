package org.atlanmod.transformation.dynamic

import org.atlanmod.model.dynamic.classModel.{ClassAttribute, ClassClass, ClassMetamodel, ClassToAttributes}
import org.atlanmod.model.dynamic.relationalModel.{ColumnToTable, RelationalColumn, RelationalTable, TableToColumns}
import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}

object Class2Relational {

    def transformation(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
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
                                        val attrs =
                                            sm.allModelLinks.filter(cl => cl.getType.equals(ClassMetamodel.CLASS_ATTRIBUTES)
                                              && cl.getSource.equals(c.head)).head.getTarget
                                        val cols = tls
                                          .filter(tl => attrs.contains(tl.getSourcePattern.head))
                                          .map(tl => tl.getTargetElement)
                                        Some(new TableToColumns(t.asInstanceOf[RelationalTable],
                                            cols.asInstanceOf[List[RelationalColumn]]))
                                    }
                                )
                            )
                        ))
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(ClassMetamodel.ATTRIBUTE),
                    from = (_, l) => Some(!l.head.asInstanceOf[ClassAttribute].isDerived),
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
                                        val class_ = // get class of a
                                            sm.allModelLinks.filter(cl => cl.isInstanceOf[ClassToAttributes]
                                              && cl.getTarget.contains(a.head)).head.getSource.asInstanceOf[ClassClass]
                                        val tb =
                                            tls.filter(tl => tl.getSourcePattern.contains(class_))
                                              .map(tl => tl.getTargetElement).head.asInstanceOf[RelationalTable]
                                        Some(new ColumnToTable(c.asInstanceOf[RelationalColumn], tb))
                                    }
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}
