package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{TraceLink, TraceLinks}

import scala.collection.mutable

class TraceLinksMap[SME, TME](map: mutable.Map[List[SME], List[TraceLink[SME, TME]]]) extends TraceLinks[SME, TME] {

    def this(){
        this(new mutable.HashMap[List[SME], List[TraceLink[SME, TME]]]())
    }

    override def find(sp: List[SME])(p: TraceLink[SME, TME] => Boolean): Option[TraceLink[SME, TME]] =
        map.get(sp) match {
            case Some(tls) => tls.find(p)
            case None => None
        }

    override def getTargetElements: List[TME] = map.flatMap(tl => tl._2.map(t => t.getTargetElement)).toList

    override def getSourcePatterns: List[List[SME]] = map.keys.toList

    def put(key: List[SME], value: TraceLink[SME, TME]): Unit = {
        map.get(key) match {
            case Some(tls) => map.put(key, value :: tls)
            case None => map.put(key, List(value))
        }
    }

    def put(key: List[SME], value: List[TraceLink[SME, TME]]): Unit = {
        map.get(key) match {
            case Some(tls) => map.put(key, value ++ tls)
            case None => map.put(key, value)
        }
    }

}
