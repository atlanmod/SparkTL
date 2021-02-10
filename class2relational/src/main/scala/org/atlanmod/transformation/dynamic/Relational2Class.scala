package org.atlanmod.transformation.dynamic

import org.atlanmod.model.classmodel._
import org.atlanmod.model.relationalmodel._
import org.atlanmod.model.{DynamicElement, DynamicLink, DynamicMetamodel}
import org.atlanmod.tl.engine.Resolve
import org.atlanmod.tl.model.Transformation
import org.atlanmod.tl.model.impl.{OutputPatternElementImpl, OutputPatternElementReferenceImpl, RuleImpl, TransformationImpl}
import org.atlanmod.tl.util.ListUtils

object Relational2Class {

    def class2relational(): Transformation[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink] = {
        val rmm =  new DynamicMetamodel[DynamicElement, DynamicLink]()
        new TransformationImpl[DynamicElement, DynamicLink, String, DynamicElement, DynamicLink](
            List()
        )
    }

}
