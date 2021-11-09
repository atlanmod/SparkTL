package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{TraceLink, TraceLinks}
import org.atlanmod.tl.util.ListUtils

class TraceLinksArray[SME, TME](tls: Array[TraceLink[SME, TME]]) extends TraceLinks[SME, TME] {

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        tls.find(tl => tl.getSourcePattern.equals(sp) && p(tl))

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] =
        new TraceLinksArray(tls.filter(tl => p(tl)))

    override def getTargetElements: Iterable[TME] = tls.map(tl => tl.getTargetElement)

    override def getSourcePatterns: Iterable[List[SME]] = tls.map(tl => tl.getSourcePattern)

    def asList(): List[TraceLink[SME, TME]] = tls.toList

    override def equals(obj: Any): Boolean = {
        obj match {
            case tl: TraceLinks[SME,TME] => ListUtils.eqList(tl.asList(), this.asList())
            case _ => false
        }
    }
}