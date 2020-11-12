package org.atlanmod

import org.atlanmod.io.{ELink, EMFMetamodel, EMFModel}
import org.atlanmod.tl.model.Rule
import org.eclipse.emf.ecore.{EClass, EObject}

object Class2Relational {

    def getClassModel(): (EMFMetamodel, EMFModel) =
        // TODO
        ???

    // Coq notations:
    //    (* Rule *)
    //    Notation "'rule' rulename 'from' types 'where' guard 'for' iterator 'to' outputpattern " :=
    //    (concreteRule rulename types (Some guard) (Some iterator) outputpattern)
    //    (right associativity, at level 60):coqtl.
    //
    //    (* Rule without guard *)
    //    Notation "'rule' rulename 'from' types 'for' iterator 'to' outputpattern " :=
    //    (concreteRule rulename types (None) (Some iterator) outputpattern)
    //    (right associativity, at level 60):coqtl.
    //
    //    (* Rule without iterator *)
    //    Notation "'rule' rulename 'from' types 'where' guard 'to' outputpattern " :=
    //    (concreteRule rulename types (Some guard) (None) outputpattern)
    //    (right associativity, at level 60):coqtl.
    //
    //    (* Rule without guard and iterator *)
    //    Notation "'rule' rulename 'from' types 'to' outputpattern " :=
    //    (concreteRule rulename types (None) (None) outputpattern)
    //    (right associativity, at level 60):coqtl.



//    rule Class2Table {
//        from
//        c : Class
//        to
//        tab: Table (
//              id <- c.id,
//              name <- c.name,
//              columns <- c.attributes->collect(a | thisModule.resolve(a, 'col'))
//            )
//    }
//    rule Attribute2Column {
//        from
//        a : Attribute (not a.derived)
//        to
//        col: Column (
//              id <- a.id,
//              name <- a.name,
//              reference <- thisModule.resolve(a.type, 'tab')
//            )
//    }



    //    name: String,
    //    types: List[SMC],
    //    from: (Model[SME, SML], List[SME]) => Option[Boolean],
    //    itExpr: (Model[SME, SML], List[SME]) => Option[Int],
    //    to: List[OutputPatternElement[SME, SML, TME, TML]]

    private def getRules(): List[Rule[EObject, ELink, EClass, EObject, ELink]] = {
        var res : List[Rule[EObject, ELink, EClass, EObject, ELink]] = List()
//        val rule_Class2Table = new RuleImpl("Class2Table", List(EClass), List())
        res
    }

}
