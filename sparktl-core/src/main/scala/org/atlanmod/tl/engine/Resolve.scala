package org.atlanmod.tl.engine

import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks}
import org.atlanmod.tl.util.ListUtils

object Resolve {

    private def resolveIter[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls: TraceLinks[STL, TTL],
                                                  sm: Model[SME, SML], tmm: Metamodel[ID,TME,TML,TMC,TMR],
                                                  name: String, t: TMC, sp: List[STL], iter: Int)
    : Option[TTL] = {
        tls.find(sp)(tl => tl.getIterator.equals(iter) && tl.getName.equals(name)) match {
//            case Some(tl2) => tmm.toModelClass(t, tl2.getTargetElement)
            case Some(tl2) => Some(tl2.getTargetElement)
            case None => None
        }
    }

    def resolveAllIter[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls: TraceLinks[STL, TTL], sm: Model[SME,SML],
                                                           tmm: Metamodel[ID,TME,TML,TMC,TMR], name: String, t: TMC,
                                                           sps: Iterable[List[STL]], iter: Int)
    : Option[Iterable[TTL]] =
        Some(sps.flatMap(l => ListUtils.optionToList(resolveIter(tls, sm, tmm, name, t, l, iter))))

    def resolve[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls : TraceLinks[STL, TTL], sm: Model[SME,SML],
                                                    tmm: Metamodel[ID, TME, TML, TMC, TMR], name: String, t: TMC,
                                                    sp: List[STL])
    : Option[TTL] = resolveIter(tls, sm, tmm, name, t, sp, 0)

    def resolveAll[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls : TraceLinks[STL,TTL], sm: Model[SME,SML],
                                                       tmm: Metamodel[ID,TME,TML,TMC,TMR], name: String, t: TMC,
                                                       sps: Iterable[List[STL]])
    : Option[Iterable[TTL]] = resolveAllIter(tls, sm, tmm, name, t, sps, 0)


    def maybeResolve[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls : TraceLinks[STL, TTL], sm: Model[SME,SML],
                                                         tmm: Metamodel[ID,TME,TML,TMC,TMR], name: String, t: TMC,
                                                         sp: Option[List[STL]])
    : Option[TTL] =
        sp match {
            case Some(sp2) => resolve(tls, sm, tmm, name, t, sp2)
            case None => None
        }

    def maybeResolveAll[ID,SME,SML,TME,TML,TMC,TMR,STL,TTL](tls : TraceLinks[STL,TTL], sm: Model[SME,SML],
                                                            tmm: Metamodel[ID,TME,TML,TMC,TMR], name: String, t: TMC,
                                                            sp: Option[List[List[STL]]])
    : Option[Iterable[TTL]] =
        sp match {
            case Some(sp2) => resolveAll(tls, sm, tmm, name, t, sp2)
            case None => None
        }

}
