package org.atlanmod.parallel.generated

import org.atlanmod.ELink
import org.atlanmod.tl.model.impl._
import org.atlanmod.tl.model.{TraceLink, Transformation}
import org.atlanmod.wrapper.{EClassWrapper, ELinkWrapper, EObjectWrapper}
import org.eclipse.emf.ecore.EObject

// WARNING : Does not work !
object Class2Relational {

    private val classPackage = org.atlanmod.model.generated.classModel.ClassPackage.eINSTANCE
    private val relationalPackage = org.atlanmod.model.generated.relationalModel.RelationalPackage.eINSTANCE

    private def removeWrapperTL(tl: TraceLink[EObjectWrapper, EObjectWrapper]): TraceLinkImpl[EObject, EObject] = {
        new TraceLinkImpl((
          tl.getSourcePattern.map(ow => ow.unwrap),
          tl.getIterator,
          tl.getName
        ), tl.getTargetElement.unwrap)
    }

    def transformation(): Transformation[EObjectWrapper, ELinkWrapper, EClassWrapper, EObjectWrapper, ELinkWrapper] = {
        new TransformationImpl[EObjectWrapper, ELinkWrapper, EClassWrapper, EObjectWrapper, ELinkWrapper](
            List(
                new RuleImpl(
                    name = "Class2Table",
                    // CoqTL : use a label instead of an EClass :
                    types = List(new EClassWrapper(classPackage.getClass_)),
                    from = (_, _) => Some(true), // No guard condition
                    itExpr = (_, _) => Some(1), // No iterator
                    to =
                      List(
                          new OutputPatternElementImpl(
                              name = "tab",
                              elementExpr = (_, _, l) => {
                                  val _class = l.head.unwrap.asInstanceOf[org.atlanmod.model.generated.classModel.Class]
                                  val table = relationalPackage.getRelationalFactory.createTable()
                                  table.setId(_class.getId)
                                  table.setName(_class.getName)
                                  Some(new EObjectWrapper(table))
                              },

                              outputElemRefs = List(
                                  new OutputPatternElementReferenceImpl(
                                      (tls, _, _, c, t) => {
                                          val attrs = c.map(o => o.unwrap).head.asInstanceOf[org.atlanmod.model.generated.classModel.Class].getAttributes
                                          val cols = tls // all the tracelink
                                            // Remove wrappers
                                            .map(tl => removeWrapperTL(tl))
                                            // get in tls, all the TraceLink, where all the elements in source are in attrs.
                                            .filter(tl => attrs.contains(tl.getSourcePattern.head))
                                            .map(tl => tl.getTargetElement)
                                          Some(new ELinkWrapper(
                                              new ELink(t.unwrap, relationalPackage.getTable_Columns, cols)
                                          ))
                                      }
                                  )
                              )
                          )
                      )
                ),
                new RuleImpl(
                    name = "Attribute2Column",
                    types = List(new EClassWrapper(classPackage.getAttribute)),
                    from = (_, l) => Some(l.head.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute].isDerived),
                    itExpr = (_, _) => Some(1), // No where clause
                    to =
                      List(
                          new OutputPatternElementImpl(
                              name = "col",
                              elementExpr = (_, _, l) => {
                                  val attribute = l.head.unwrap.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute]
                                  val column = relationalPackage.getRelationalFactory.createColumn()
                                  column.setId(attribute.getId)
                                  column.setName(attribute.getName)
                                  Some(new EObjectWrapper(column))
                              },
                              outputElemRefs = List(
                                  new OutputPatternElementReferenceImpl(
                                      (tls, _, _, a, c) => {
                                          // get the class related to a
                                          val cl =
                                              a.head.asInstanceOf[org.atlanmod.model.generated.classModel.Attribute].eContainer()
                                                .asInstanceOf[org.atlanmod.model.generated.classModel.Class]
                                          val tb =
                                              tls
                                                // Remove wrappers
                                                .map(tl => removeWrapperTL(tl)) // All the trace links
                                                .filter(tl => {
                                                    tl.getSourcePattern.contains(cl)
                                                }) // Get the ones whose source is the class
                                                .map(tl => tl.getTargetElement) // Get the corresponding tables
                                          Some(new ELinkWrapper(new ELink(c.unwrap, relationalPackage.getColumn_Reference, tb)))
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
