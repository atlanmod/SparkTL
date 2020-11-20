package org.atlanmod.tl.sequential

import org.atlanmod.tl.model.{Metamodel, Model, TraceLink}
import org.atlanmod.tl.util.ListUtils

object Resolve {


    private def resolveIter [SME, SML, TME, TML, TMC, TMR](tlr: List[TraceLink[SME,TME]], sm: Model[SME,SML],
                                                           mm: Metamodel[TME, TML, TMC, TMR],
                                                           name: String, typ: TMC, sp: List[SME], iter: Int)
    : Option[TMC] = {
        /*
         let tl := find (fun tl: @TraceLink SourceModelElement TargetModelElement =>
         match tl with
          buildTraceLink (sp', iter', name') _ =>
            (list_beq SourceModelElement beq_ModelElement sp' sp) &&
            (iter' =? iter) && (name =? name')%string
         end) tls in
        match tl with
         | Some tl' => toModelClass type (TraceLink_getTargetElement tl')
         | None => None
        end.
  */
        val tl = tlr.find(tl => sp == tl.getSourcePattern
          & iter == tl.getIterator
          & name == tl.getName)
        tl match {
            case Some(tl2) => mm.toModelClass(typ, tl2.getTargetElement)
            case None => None
        }
    }

    private def resolve[SME, SML, TME, TML, TMC, TMR](tr: List[TraceLink[SME,TME]], sm: Model[SME,SML],
                                            mm: Metamodel[TME, TML, TMC, TMR],
                                            name: String, typ: TMC, sp: List[SME])
    : Option[TMC] =
        resolveIter(tr, sm, mm, name, typ, sp, 0)

    private def resolveAllIter[SME, SML, TME, TMC](tr: List[TraceLink[SME, TME]], sm: Model[SME,TME], name: String,
                                                   typ: TMC, sps: List[List[SME]], iter: Int)
    : Option[List[TMC]] =
        Some(sps.flatMap(l => ListUtils.optionToList(resolveIter(tr, sm, mm, name, typ, l, iter))))

    private def resolveAll[SME, SML, TME, TMC](tr: List[TraceLink[SME, TME]], sm: Model[SME,TME], name: String,
                                               typ: TMC, sps: List[List[SME]])
    : Option[List[TMC]] =
        resolveAllIter(tr, sm, name, typ, sps, 0)

    private def maybeResolve[SME, SML, TME, TMC](tr: List[TraceLink[SME, TME]], sm: Model[SME,TME], name: String,
                                                 typ: TMC, sp: Option[List[SME]])
    : Option[TMC] =
        sp match {
            case Some(sp2) => resolve(tr, sm, name, typ, sp2)
            case None => None
        }

    private def  maybeResolve[SME, SML, TME, TMC](tr: List[TraceLink[SME, TME]], sm: Model[SME,TME], name: String,
                                                  typ: TMC, sp: Option[List[List[SME]]])
    : Option[List[TMC]] =
        sp match {
            case Some(sp2) => resolveAll(tr, sm, name, typ, sp2)
            case None => None
        }
}
