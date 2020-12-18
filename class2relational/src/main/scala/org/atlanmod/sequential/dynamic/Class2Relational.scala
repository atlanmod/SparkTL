package org.atlanmod.sequential.dynamic

import org.atlanmod.model.dynamic.{DynamicElement, DynamicLink}
import org.atlanmod.model.generated.{classModel, relationalModel}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}

object Class2Relational {

    private val classPackage = classModel.ClassPackage.eINSTANCE
    private val relationalPackage = relationalModel.RelationalPackage.eINSTANCE

    def transformation(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    types = List(classPackage.getClass_.toString),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _) => Some(1), // No iterator
                    to = List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            elementExpr = (_, _, l) => {
                                val _class = l.head
                                val table = new DynamicElement(relationalPackage.getTable.getName)
                                table.eSet("id", _class.eGet("id"))
                                table.eSet("name", _class.eGet("name"))
                                Some(table)
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, c, t) => {
                                        val attrs = sm.allModelLinks.filter(dl => dl.getSource.equals(c.head)
                                            && dl.getType.equals(classPackage.getClass_Attributes.getName)).head.getTarget
                                        val cols = tls
                                            .filter(tl => attrs.contains(tl.getSourcePattern.head))
                                            .map(tl => tl.getTargetElement)
                                        Some(new DynamicLink(relationalPackage.getTable_Columns.getName, t, cols))
                                    }
                                )
                            )
                    ))
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(classPackage.getAttribute.toString),
                    from = (_, l) => Some(l.head.eGet("derived").asInstanceOf[Boolean]),
                    itExpr = (_, _) => Some(1), // No where clause
                    to = List(
                        new OutputPatternElementImpl(
                            name = "col",
                            elementExpr = (_,_,l) => {
                                val attribute = l.head
                                val column = new DynamicElement(relationalPackage.getColumn.getName)
                                column.eSet("name", attribute.eGet("name"))
                                column.eSet("id", attribute.eGet("id"))
                                Some(column)
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    (tls, _, sm, a, c) => {
                                        val cl = sm.allModelLinks.filter(dl => dl.getSource.equals(a.head)
                                          && dl.getType.equals(classPackage.getAttribute_Type.getName)).head.getTarget
                                        val tb =
                                            tls.filter(tl => tl.getSourcePattern.contains(cl)).map(tl => tl.getTargetElement)
                                        Some(new DynamicLink(relationalPackage.getColumn_Reference.getName, c, tb))
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
