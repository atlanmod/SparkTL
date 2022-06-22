package org.atlanmod.tl.model

trait TraceLinks[SME, TME] extends ITraceLinks {

    def getSourcePatterns: Iterable[List[SME]]

    def getTargetElements: Iterable[TME]

    def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]]

    def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME]

    def equals(o: Any): Boolean

    def asList(): List[TraceLink[SME, TME]]

    def getIterableSeq(): Seq[Any]
}
