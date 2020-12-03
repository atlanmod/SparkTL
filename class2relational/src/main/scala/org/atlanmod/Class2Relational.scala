package org.atlanmod

import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.eclipse.emf.ecore.{EClass, EObject}

// Is Multivalued -> derived

class Class2Relational() {

    private val classPackage =  classModel.ClassPackage.eINSTANCE
    private val relationalPackage =  relationalModel.RelationalPackage.eINSTANCE

    private def transformation() : Transformation[EObject, ELink, EClass, EObject, ELink] = {

        new TransformationImpl[EObject, ELink, EClass, EObject, ELink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                  // CoqTL : use a label instead of an EClass :
                    types = List(classPackage.getClass_),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _)  => Some(1), // No iterator
                    to =
                      List(
                        new OutputPatternElementImpl(
                            name = "tab",
                            elementExpr = (_, _, l) => {
                                val _class = l.head.asInstanceOf[classModel.Class]
                                val table = relationalPackage.getRelationalFactory.createTable()
                                table.setId(_class.getId)
                                table.setName(_class.getName)
                                Some(table)
                            },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    /*
                                    tls: List[TraceLink[EObject, EObject]]
                                    i: Int
                                    m: Model[EObject, ELink]
                                    c: List[EObject]
                                    t: EObject
                                    => Option[TML]
                                    */
                                    (tls, i, m, c, t) => {
                                        val attrs = m.allModelElements.filter(o => o.eClass() == classPackage.getAttribute)
//                                        Some(new ELink(t , new EReferenceImpl(), attrs(i)))
                                        // TODO
                                        None
                                    }
                                )
                            )
                        )
                    )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(classPackage.getAttribute),
                    from = (_, l) => Some(l.asInstanceOf[classModel.Attribute].isDerived),
                    itExpr = (_, _) => Some(1), // No where clause
                    to =
                      List(
                        new OutputPatternElementImpl(
                            name = "col",
                            elementExpr = (_, _, l) =>
                                {
                                    val attribute = l.head.asInstanceOf[classModel.Attribute]
                                    val column =  relationalPackage.getRelationalFactory.createColumn()
                                    column.setId(attribute.getId)
                                    column.setName(attribute.getName)
                                    Some(column)
                                },
                            outputElemRefs = List(
                                new OutputPatternElementReferenceImpl(
                                    /*
                                    tls: List[TraceLink[EObject, EObject]]
                                    i: Int
                                    m: Model[EObject, ELink]
                                    a: List[Eobject]
                                    c: EObject
                                    => Option[TML]
                                    */
                                    (tls, i, m, a, c) => {
                                        val cls = m.allModelElements.filter(o => o.eClass() == classPackage.getClass_)
//                                        Some(new ELink(c , new EReferenceImpl(), cls(i)))
                                        // TODO
                                        None
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
