package org.atlanmod.class2relational.model.transformation.dynamic

import org.atlanmod.class2relational.model.ModelSamples.{getClassModelSample, getRelationalModelSample}
import org.atlanmod.class2relational.model.classmodel.ClassMetamodel
import org.atlanmod.class2relational.model.relationalmodel.{RelationalElement, RelationalLink, RelationalModel}
import org.atlanmod.class2relational.transformation.dynamic.Class2Relational
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestClass2RelationalSeq extends AnyFunSuite {

    test("simple equals to the right result") {
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation, model, metamodel,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val expected = getRelationalModelSample
        assert(result.equals(expected))
    }

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
        assert(result_simple.equals(result_byrule))
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
        assert(result_simple.equals(result_twophase))
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
        assert(result_simple.equals(result_twophase))
    }

}
