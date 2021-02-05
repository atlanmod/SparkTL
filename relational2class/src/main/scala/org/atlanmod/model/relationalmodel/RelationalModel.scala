package org.atlanmod.model.relationalmodel

import org.atlanmod.model.DynamicModel

class RelationalModel(elements: List[RelationalElement] = List(), links: List[RelationalLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[RelationalElement] = elements
    override def allModelLinks: List[RelationalLink] = links

}