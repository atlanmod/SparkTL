package org.atlanmod.tl.engine

import org.atlanmod.tl.model._

object Eval {

    def evalGuardExpr[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML, SMC], sp: List[SME])
    : Option[Boolean] =
        r.getGuardExpr(sm, sp)


    def evalIteratorExpr[SME, SML, SMC, TME, TML](r: Rule[SME, SML, SMC, TME, TML], sm: Model[SME, SML, SMC], sp: List[SME])
    : Int =
        r.getIteratorExpr(sm, sp) match {
            case Some(n) => n
            case _ => 0
        }


    def evalOutputPatternElementExpr[SME, SML, SMC, TME, TML](sm: Model[SME, SML, SMC], sp: List[SME], iter: Int,
                                                              o: OutputPatternElement[SME, SML, SMC, TME, TML])
    : Option[TME] =
        o.getElementExpr(iter, sm, sp)

    def evalOutputPatternLinkExpr[SME, SML, SMC, TME, TML](sm: Model[SME, SML, SMC], sp: List[SME], oe: TME, iter: Int,
                                  tr: TraceLinks[SME, TME], o: OutputPatternElementReference[SME, SML, SMC, TME, TML])
    : Option[TML]= {
        try {
            o.getLinkExpr(tr, iter, sm, sp, oe)
        }catch {
            case _: Exception => None
        }
    }

}
