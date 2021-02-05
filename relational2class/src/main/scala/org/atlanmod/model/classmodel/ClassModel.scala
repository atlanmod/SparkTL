package org.atlanmod.model.classmodel

import org.atlanmod.model.DynamicModel

class ClassModel(elements: List[ClassElement] = List(), links: List[ClassLink] = List())
  extends DynamicModel(elements, links){

    override def allModelElements: List[ClassElement] = elements
    override def allModelLinks: List[ClassLink] = links

}

