package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{TraceLink, TraceLinks}
import org.atlanmod.tl.util.ListUtils

class TraceLinksMap[SME, TME](map: scala.collection.immutable.Map[List[SME], List[TraceLink[SME, TME]]]) extends TraceLinks[SME, TME] {

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        map.get(sp) match {
            case Some(tls) => tls.find(p)
            case None => None
        }

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] =
        {
            var tmp: List[(List[SME], List[TraceLink[SME, TME]])]  = List()
            for (key <- map.keys){
                map.get(key) match {
                    case Some(tls) =>
                        tls.filter(p) match {
                            case h :: t => tmp = (key, h::t) :: tmp
                            case _ =>
                        }
                    case _ =>
                }
            }
            new TraceLinksMap(tmp.toMap)
        }

    override def getTargetElements: List[TME] = map.flatMap(tl => tl._2.map(t => t.getTargetElement)).toList

    override def getSourcePatterns: List[List[SME]] = map.keys.toList

    def keys() : Iterable[List[SME]] = {
        map.keys
    }

    def get(key: List[SME]) : Option[List[TraceLink[SME, TME]]] = {
        map.get(key)
    }


    def getMap(): scala.collection.immutable.Map[List[SME], List[TraceLink[SME, TME]]] = {
        map
    }

    def asList(): List[TraceLink[SME, TME]] = map.toList.flatMap(t => t._2)

    override def equals(obj: Any): Boolean = {
        obj match {
            case tl: TraceLinks[SME,TME] =>
                ListUtils.eqList(tl.asList(), this.asList())
            case _ =>
                false
        }
    }

}
