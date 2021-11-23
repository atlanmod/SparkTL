package org.atlanmod.tl.engine

import org.atlanmod.tl.model.{Metamodel, Model, TraceLinks}
import org.atlanmod.tl.util.ListUtils

object Resolve {

    private def resolveIter[SME, SML, SMC, TME, TML, TMC, TMR](tls: TraceLinks[SME, TME],
                                                  sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                                  name: String, t: TMC, sp: List[SME], iter: Int)
    : Option[TME] = {
        tls.find(sp)(tl => tl.getIterator.equals(iter) && tl.getName.equals(name)) match {
            case Some(tl2) => tmm.toModelClass(t, tl2.getTargetElement)
            case None => None
        }
    }


    def resolveAllIter[SME, SML, SMC, TME, TML, TMC, TMR](tls: TraceLinks[SME, TME],
                                                     sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                                     name: String, t: TMC, sps: Iterable[List[SME]], iter: Int)
    : Option[Iterable[TME]] =
        Some(sps.flatMap(l => ListUtils.optionToList(resolveIter(tls, sm, tmm, name, t, l, iter))))

    // ----------------------------------------------------------------------------------------------------

    def resolve[SME, SML, SMC, TME, TML, TMC, TMR](tls : TraceLinks[SME, TME],
                                              sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                              name: String, t: TMC, sp: List[SME])
    : Option[TME] = resolveIter(tls, sm, tmm, name, t, sp, 0)

    def resolveAll[SME, SML, SMC, TME, TML, TMC, TMR](tls : TraceLinks[SME, TME],
                                                 sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                                 name: String, t: TMC, sps: Iterable[List[SME]])
    : Option[Iterable[TME]] = resolveAllIter(tls, sm, tmm, name, t, sps, 0)


    def maybeResolve[SME, SML, SMC, TME, TML, TMC, TMR](tls : TraceLinks[SME, TME],
                                                      sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                                      name: String, t: TMC, sp: Option[List[SME]])
    : Option[TME] =
        sp match {
            case Some(sp2) => resolve(tls, sm, tmm, name, t, sp2)
            case None => None
        }

    def maybeResolveAll[SME, SML, SMC, TME, TML, TMC, TMR](tls : TraceLinks[SME, TME],
                                                      sm: Model[SME, SML], tmm: Metamodel[TME, TML, TMC, TMR],
                                                      name: String, t: TMC, sp: Option[List[List[SME]]])
    : Option[Iterable[TME]] =
        sp match {
            case Some(sp2) => resolveAll(tls, sm, tmm, name, t, sp2)
            case None => None
        }

}
