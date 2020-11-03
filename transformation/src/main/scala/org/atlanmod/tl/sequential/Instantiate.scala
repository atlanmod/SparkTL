package org.atlanmod.tl.sequential

object Instantiate {

    //    private def evalGuardExpr(r: Rule[SME, SML, SMC, TME, TML], sm: SourceModelType, sp: List[SME])
    //    : Option[Boolean] =
    //        r.getGuardExpr(sm, sp)
    //
    //    private def evalIteratorExpr(r: Rule[SME, SML, SMC, TME, TML], sm: SourceModelType, sp: List[SME])
    //    : Int =
    //        r.getIteratorExpr(sm, sp) match {
    //            case Some(n) => n
    //            case _ => 0
    //        }
    //
    //    private def evalOutputPatternElementExpr(sm: SourceModelType, sp: List[SME], iter: Int,
    //                                             o: OutputPatternElement[SME, SML, TME, TML])
    //    : Option[TME] = o.getElementExpr(iter, sm, sp)
    //
    //    private def evalOutputPatternLinkExpr(sm: SourceModelType, sp: List[SME], oe: TME, iter: Int,
    //                                          tr: List[TraceLinkType],
    //                                          o: OutputPatternElementReference[SME, SML, TME, TML])
    //    : Option[TML] = o.getLinkExpr(tr, iter, sm, sp, oe)


    def instantiatePattern[SME, SML, SMC, TME, TML](value: Transformation[SME, SML, SMC, TME, TML],
                                                    sm: Model[SME, SML])={
        //TODO
    }
}
