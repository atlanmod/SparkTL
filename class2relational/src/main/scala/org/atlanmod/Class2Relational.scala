package org.atlanmod

import org.atlanmod.io.{ELink, EMFMetamodel, EMFModel}
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.model.{Model, OutputPatternElement, Rule, Transformation}
import org.eclipse.emf.common.notify.{Adapter, Notification}
import org.eclipse.emf.common.util.{EList, TreeIterator}
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.{EAttribute, EClass, EObject, EOperation, EReference, EStructuralFeature}

//import org.atlanmod.zoo.

class Class2Relational() {

    def getClassModel(): (EMFMetamodel, EMFModel) =
        // TODO
        ???

    /*
     Coq notations:
        (* Rule *)
        Notation "'rule' rulename 'from' types 'where' guard 'for' iterator 'to' outputpattern " :=
        (concreteRule rulename types (Some guard) (Some iterator) outputpattern)
        (right associativity, at level 60):coqtl.

        (* Rule without guard *)
        Notation "'rule' rulename 'from' types 'for' iterator 'to' outputpattern " :=
        (concreteRule rulename types (None) (Some iterator) outputpattern)
        (right associativity, at level 60):coqtl.

        (* Rule without iterator *)
        Notation "'rule' rulename 'from' types 'where' guard 'to' outputpattern " :=
        (concreteRule rulename types (Some guard) (None) outputpattern)
        (right associativity, at level 60):coqtl.

        (* Rule without guard and iterator *)
        Notation "'rule' rulename 'from' types 'to' outputpattern " :=
        (concreteRule rulename types (None) (None) outputpattern)
        (right associativity, at level 60):coqtl.
    */

    private def rule_Class2Table(mm : EMFMetamodel): Rule[EObject, ELink, EClass, EObject, ELink] = {
        """
            rule  {
                from
                c : Class
                to
                tab: Table (
                      id <- c.id,
                      name <- c.name,
                      columns <- c.attributes->collect(a | thisModule.resolve(a, 'col'))
                    )
            }

            (* Rule without guard *)
            Notation "'rule' rulename 'from' types 'for' iterator 'to' outputpattern " :=
            (concreteRule rulename types (None) (Some iterator) outputpattern)
            (right associativity, at level 60):coqtl.

            rule "Class2Table"
            from [ClassClass]
            to [elem [ClassClass] TableClass "tab"
                (fun i m c => BuildTable (getClassId c) (getClassName c))
                [link [ClassClass] TableClass TableColumnsReference
                  (fun tls i m c t =>
                    maybeBuildTableColumns t
                      (maybeResolveAll tls m "col" ColumnClass
                        (maybeSingletons (getClassAttributesObjects c m))))]]
            ;

        """
        val name = "Class2Table"
        val types: List[EClass] = List(EClass)
        val from: (Model[EObject, ELink], List[EObject]) => Option[Boolean] = (_, _) => Some(true) // No guard condition
        val iterator: (Model[EObject, ELink], List[EObject]) => Option[Int] = (_, _)  => Some(0) // No iterator

        //        name: String,
        //     elementExpr: (Int, Model[SME, SML], List[SME]) => Option[TME],
        //     outputElemRefs: List[OutputPatternElementReference[SME, SML, TME, TML]]

        val output_tab : OutputPatternElement[EObject, ELink, EObject, ELink] =
            new OutputPatternElementImpl(
                name = "tab",
                elementExpr = (i, m, l) => None, // TODO
                outputElemRefs = List() // TODO
            )

        val to: List[OutputPatternElement[EObject, ELink, EObject, ELink]] = List(output_tab)
        new RuleImpl(name, types, from, iterator, to)
    }

    private def rule_Attribute2Column(): Rule[EObject, ELink, EClass, EObject, ELink] = {
        """
            rule Attribute2Column {
                from
                a : Attribute (not a.derived)
                to
                col: Column (
                      id <- a.id,
                      name <- a.name,
                      reference <- thisModule.resolve(a.type, 'tab')
                    )
            }

            rule "Attribute2Column"
            from [AttributeClass]
            where (fun m a => negb (getAttributeDerived a))
            to [elem [AttributeClass] ColumnClass "col"
                (fun i m a => BuildColumn (getAttributeId a) (getAttributeName a))
                [link [AttributeClass] ColumnClass ColumnReferenceReference
                  (fun tls i m a c =>
                    maybeBuildColumnReference c
                      (maybeResolve tls m "tab" TableClass
                        (maybeSingleton (getAttributeTypeObject a m))))]]
        """
        val name = "Attribute2Column"
        val types: List[EClass] = List(EClass)
        val from: (Model[EObject, ELink], List[EObject]) => Option[Boolean] = (m, l) => Some(true)
        val iterator: (Model[EObject, ELink], List[EObject]) => Option[Int] = (m, l) => Some(0)
        val to: List[OutputPatternElement[EObject, ELink, EObject, ELink]] = List()
        new RuleImpl(name, types, from, iterator, to)
    }


    private def getRules(): List[Rule[EObject, ELink, EClass, EObject, ELink]] = {
        List(rule_Class2Table(), rule_Attribute2Column())
    }

    private def getTransformation(): Transformation[EObject, ELink, EClass, EObject, ELink] = {
        new TransformationImpl(getRules())
    }

}
