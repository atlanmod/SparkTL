package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.TraceLink

class TraceLinkImpl[SME, TME](spin: (List[SME], Int, String), te: TME)  extends TraceLink[SME, TME]  {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     */

    // Accessors
    override def getSourcePattern: List[SME] = spin._1
    override def getIterator: Int = spin._2
    override def getName: String = spin._3
    override def getTargetElement: TME = te

}
