package org.atlanmod.tl.model

trait TraceLinks[STL, TTL]{

    def getSourcePatterns: Iterable[List[STL]]

    def getTargetElements: Iterable[TTL]

    def find(sp: List[STL])(p: TraceLink[STL, TTL] => Boolean): Option[TraceLink[STL, TTL]]

    def filter(p: TraceLink[STL, TTL] => Boolean): TraceLinks[STL, TTL]

    def equals(o: Any): Boolean

    def asList(): List[TraceLink[STL, TTL]]

    def getIterableSeq():Either[Seq[(List[STL], String)], Seq[List[STL]]]
}
