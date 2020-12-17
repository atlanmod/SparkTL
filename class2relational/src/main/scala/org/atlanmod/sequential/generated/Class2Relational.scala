package org.atlanmod.sequential.generated

import org.atlanmod.ELink
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.eclipse.emf.ecore.{EClass, EObject}

object Class2Relational {
    private val classPackage = org.atlanmod.model.generated.classModel.ClassPackage.eINSTANCE
    private val relationalPackage = org.atlanmod.model.generated.relationalModel.RelationalPackage.eINSTANCE

    def transformation(): Transformation[EObject, ELink, EClass, EObject, ELink] = {

        new TransformationImpl[EObject, ELink, EClass, EObject, ELink](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    // CoqTL : use a label instead of an EClass :
                    types = List(classPackage.getClass_),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _) => Some(1), // No iterator
                    to =
                      List(
                          new OutputPatternElementImpl(
                              name = "tab",
                              elementExpr = (_, _, l) => {
                                  val _class = l.head.asInstanceOf[org.atlanmod.model.generated.classModel.Class]
                                  val table = relationalPackage.getRelationalFactory.createTable()
                                  table.setId(_class.getId)
                                  table.setName(_class.getName)
                                  Some(table)
                              },

                              outputElemRefs = List(
                                  new OutputPatternElementReferenceImpl(
                                      (tls, _, _, c, t) => {
                                          val attrs = c.head.asInstanceOf[org.atlanmod.model.generated.classModel.Class].getAttributes
                                          val cols = tls // all the tracelink
                                            // get in tls, all the TraceLink, where all the elements in source are in attrs.
                                            .filter(tl => attrs.contains(tl.getSourcePattern.head))
                                            .map(tl => tl.getTargetElement)
                                          Some(new ELink(t, relationalPackage.getTable_Columns, cols))
                                      }
                                  )
                              )
                          )
                      )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(classPackage.getAttribute),
                    from = (_, l) => Some(l.head.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute].isDerived),
                    itExpr = (_, _) => Some(1), // No where clause
                    to =
                      List(
                          new OutputPatternElementImpl(
                              name = "col",
                              elementExpr = (_, _, l) => {
                                  val attribute = l.head.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute]
                                  val column = relationalPackage.getRelationalFactory.createColumn()
                                  column.setId(attribute.getId)
                                  column.setName(attribute.getName)
                                  Some(column)
                              },
                              outputElemRefs = List(
                                  new OutputPatternElementReferenceImpl(
                                      (tls, _, _, a, c) => {
                                          // get the class related to a
                                          val cl =
                                              a.head.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute].eContainer()
                                                .asInstanceOf[org.atlanmod.model.generated.classModel.Class]
                                          val tb =
                                              tls // All the trace links
                                                .filter(tl => {
                                                    tl.getSourcePattern.contains(cl)
                                                }) // Get the ones whose source is the class
                                                .map(tl => tl.getTargetElement) // Get the corresponding tables
                                          Some(new ELink(c, relationalPackage.getColumn_Reference, tb))
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
