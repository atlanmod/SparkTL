package org.atlanmod.transformation.dynamic

import org.atlanmod.model.ModelGenerator.getClassModelSample
import org.atlanmod.model.classmodel.ClassMetamodel
import org.atlanmod.model.relationalmodel.{RelationalElement, RelationalLink, RelationalModel}
import org.atlanmod.model.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestTransformationSeq extends AnyFunSuite {

    test("simple equals to byrule") {
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val result_byrule = org.atlanmod.tl.engine.sequential.TransformationEngineByRule.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        assert(result_simple.weak_equals(result_byrule))
    }

    test("simple equals to twophase") {
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val result_twophase = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        assert(result_simple.weak_equals(result_twophase))
    }

    test("simple equals to twophaseHM") {
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val result_twophase = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhaseHM.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        assert(result_simple.weak_equals(result_twophase))
    }

}
