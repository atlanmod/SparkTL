package org.atlanmod

import org.atlanmod.model.{ClassHelper, RelationalHelper}
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.eclipse.emf.ecore.{EClass, EObject}

class Class2Relational() {

    private def transformation() : Transformation[EObject, ELink, EClass, EObject, ELink] = {
        new TransformationImpl[EObject, ELink, EClass, EObject, ELink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    types = List(ClassHelper.getEClass_Class),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _)  => Some(1), // No iterator,
                    to =
//                      elem [AttributeClass] ColumnClass "col"
//                        (fun i m a => BuildColumn (getAttributeId a) (getAttributeName a))
                      List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            // TODO : why l is a list ? How to use ``elem''
                            elementExpr = (_, _, l) => Some(RelationalHelper.buildTable(
                                ClassHelper.getClass_Id(l.head), ClassHelper.getClass_name(l.head))),
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(null) // TODO
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(ClassHelper.getEClass_Attribute),
                    from = (_, l) => Some(ClassHelper.getAttribute_isDerived(l.head)), // TODO probably wrong
                    itExpr = (_, _) => Some(1),
                    to =
//                      elem [AttributeClass] ColumnClass "col"
//                        (fun i m a => BuildColumn (getAttributeId a) (getAttributeName a))
                      List(
                        new OutputPatternElementImpl(
                            name = "col",
                            // TODO : why l is a list ? How to use ``elem''
                            // ConcreteSyntax.v : elem
                            //
                            elementExpr = (_, _, l) => Some(RelationalHelper.buildColumn(
                                ClassHelper.getAttribute_Id(l.head), ClassHelper.getAttribute_name(l.head))
                            ),
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
