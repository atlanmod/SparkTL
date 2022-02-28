package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.TraceLink
import org.atlanmod.tl.util.ListUtils

class TraceLinkWithRuleImpl[SME, TME](spin: (List[SME], Int, String, String), te: TME)  extends TraceLink[SME, TME]  {
    /*
     *  SME : SourceModelElement
     *  TME : TargetModelElement
     */

    // Accessors
    override def getSourcePattern: List[SME] = spin._1
    override def getIterator: Int = spin._2
    def getRulename: String = spin._3
    override def getName: String = spin._4
    override def getTargetElement: TME = te

    override def equals(obj: Any): Boolean =
        obj match{
            case tl:TraceLink[SME, TME] =>
                ListUtils.eqList(tl.getSourcePattern, this.getSourcePattern) &
                  tl.getTargetElement.equals(this.getTargetElement)
            case _ => false
        }


}
