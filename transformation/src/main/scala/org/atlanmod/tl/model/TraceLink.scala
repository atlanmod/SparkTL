package org.atlanmod.tl.model

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
