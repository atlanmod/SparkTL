package org.atlanmod

import org.atlanmod.model.{ClassHelper, RelationalHelper}
import org.atlanmod.relationalModel.impl.{ColumnImpl, TableImpl}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.eclipse.emf.ecore.{EClass, EObject}

class Class2Relational() {

    private def transformation() : Transformation[EObject, ELink, EClass, EObject, ELink] = {

        new TransformationImpl[EObject, ELink, EClass, EObject, ELink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                  // CoqTL : use a label instead of an EClass :
                    // types = List(ClassHelper.CLASS)
                    types = List(classModel.Class),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _)  => Some(1), // No iterator
                    to =
                      List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            // TODO : why l is a list ? How to use ``elem''
                            elementExpr = (_, _, l) => {
                                val _class = l.head.asInstanceOf[classModel.Class]
                                val table = new TableImpl
                                table.setId(_class.getId)
                                table.setName(_class.getName)
                                Some(table)
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(null) // TODO
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(classModel.Attribute),
                    from = (_, l) => Some(true), // TODO : not derived
                    itExpr = (_, _) => Some(1), // No where clause
                    to =
//                      elem [AttributeClass] ColumnClass "col"
//                        (fun i m a => BuildColumn (getAttributeId a) (getAttributeName a))
                      List(
                        new OutputPatternElementImpl(
                            name = "col",
                            // TODO : why l is a list ? How to use ``elem''
                            // ConcreteSyntax.v : elem
                            //
                            elementExpr = (_, _, l) =>
                                {
                                    val attribute = l.head.asInstanceOf[classModel.Attribute]
                                    val column = new ColumnImpl
                                    column.setId(attribute.getId)
                                    column.setName(attribute.getName)
                                    Some(column)
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(null) // TODO
                            )
                        )
                    )
                )
            )
        )
    }


}
