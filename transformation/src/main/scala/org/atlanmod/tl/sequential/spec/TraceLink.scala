package org.atlanmod.tl.sequential.spec

trait TraceLink[SME, TME] {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     */

    // Accessors
    def getSourcePattern: List[SME]

    def getIterator: Int

    def getName: String

    def getTargetElement: TME
}
