package org.atlanmod.tl.model.impl

import org.atlanmod.tl.model.{TraceLink, TraceLinks}
import org.atlanmod.tl.util.ListUtils

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

    override def filter(p: TraceLink[SME, TME] => Boolean): TraceLinks[SME, TME] =
        {
            val new_map = new TraceLinksMap(new mutable.HashMap[List[SME], List[TraceLink[SME, TME]]]())
            for (key <- map.keys){
                map.get(key) match {
                    case Some(tls) =>
                       tls.filter(p) match{
                           case h::t => new_map.put(key, h::t)
                           case _ =>
                       }
                    case _ =>
                }
            }
            new_map
        }

    override def getTargetElements: List[TME] = map.flatMap(tl => tl._2.map(t => t.getTargetElement)).toList

    override def getSourcePatterns: List[List[SME]] = map.keys.toList

    def keys() : Iterable[List[SME]] = {
        map.keys
    }

    def get(key: List[SME]) : Option[List[TraceLink[SME, TME]]] = {
        map.get(key)
    }


    def getMap(): mutable.Map[List[SME], List[TraceLink[SME, TME]]] = {
        map
    }

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
