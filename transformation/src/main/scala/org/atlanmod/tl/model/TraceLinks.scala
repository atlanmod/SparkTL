package org.atlanmod.tl.model

trait TraceLinks[SME, TME] {

    def getSourcePatterns: List[List[SME]]

    def getTargetElements: List[TME]

    def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]]

}
