package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{TraceLink, TraceLinks}

class TraceLinksList[SME, TME](tls: List[TraceLink[SME, TME]]) extends TraceLinks[SME, TME] {

    def this() {
        this(List())
    }

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        tls.find(tl => tl.getSourcePattern.equals(sp)).find(p)

    override def getTargetElements: List[TME] = tls.map(tl => tl.getTargetElement)

    override def getSourcePatterns: List[List[SME]] = tls.map(tl => tl.getSourcePattern)
}