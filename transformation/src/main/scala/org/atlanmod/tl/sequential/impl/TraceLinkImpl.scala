package org.atlanmod.tl.sequential.impl

import org.atlanmod.tl.sequential.TraceLink

class TraceLinkImpl[SME, TME](spin: (List[SME], Int, String), te: TME)  extends TraceLink[SME, TME]  {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     */

    // Accessors
    override def getSourcePattern: List[SME] = spin._1
    override def getIterator: Int = spin._2
    override def getName: String = spin._3
    override def degTargetElement: TME = te

}
