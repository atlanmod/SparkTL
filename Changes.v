(** TwoPhaseSemantics.v l60-61 *)

Definition applyTraces (tr: Transformation) (sm : SourceModel) (tls: list (@TraceLink SourceModelElement TargetModelElement)): list TargetModelLink :=
  flat_map (fun sp => applyPatternTraces tr sm sp tls) (allTuples tr sm).

>>>>>>>>>>>>>>>>>>>>>>>>>>

Definition allSourcePattern (tls: list (@TraceLink SourceModelElement TargetModelElement)): list (list SourceModelElement) :=
  (* https://coq.inria.fr/stdlib/Coq.Lists.List.html#nodup *)
  nodup (map TraceLink_getSourcePattern tls).

Definition applyTraces (tr: Transformation) (sm : SourceModel) (tls: list (@TraceLink SourceModelElement TargetModelElement)): list TargetModelLink :=
  flat_map (fun sp => applyPatternTraces tr sm sp tls) (allSourcePattern tls).


(** Everywhere *)

list (@TraceLink SourceModelElement TargetModelElement) -> @TraceLinks SourceModelElement TargetModelElement
with TraceLinks:

>>>>>>>>>>>>>>>>>>>>>>>>>>

Class TraceLinks (SourceModelElement: Type) (TargetModelElement: Type) := {

    sourcePatterns: list (list SourceModelElement);

    targetElements: list TargetModelElement;

    find: forall (sp: list SourceModelElement) (p:@TraceLink SourceModelElement TargetModelElement -> bool), option (@TraceLink SourceModelElement TargetModelElement);

}.