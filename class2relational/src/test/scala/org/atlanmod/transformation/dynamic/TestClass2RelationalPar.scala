package org.atlanmod.transformation.dynamic

import org.apache.spark.SparkContext
import org.atlanmod.model.ModelSamples.{getClassModelSample, getRelationalModelSample}
import org.atlanmod.model.classmodel.ClassMetamodel
import org.atlanmod.model.relationalmodel.{RelationalElement, RelationalLink, RelationalModel}
import org.atlanmod.model.{DynamicElement, DynamicLink}
import org.atlanmod.tl.util.SparkUtils
import org.scalatest.funsuite.AnyFunSuite

class TestClass2RelationalPar extends AnyFunSuite {


    test("simple equals to the right result") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation, model, metamodel, sc,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val expected = getRelationalModelSample
        sc.stop()
        assert(result.equals(expected))
    }

    test("simple equals to byrule") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result_simple = org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(transformation, model, metamodel, sc,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val result_byrule = org.atlanmod.tl.engine.parallel.TransformationEngineByRule.execute(transformation, model, metamodel, sc,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        sc.stop()
        assert(result_simple.equals(result_byrule))
    }

    test("simple equals to twophase") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getClassModelSample
        val metamodel = ClassMetamodel.metamodel
        val transformation = Class2Relational.class2relational()
        val result_simple = org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(transformation, model, metamodel, sc,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        val result_twophase = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation, model, metamodel, sc,
            makeModel = (e: List[DynamicElement], l: List[DynamicLink])
            => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]]))
          .asInstanceOf[RelationalModel]
        sc.stop()
        assert(result_simple.equals(result_twophase))
    }

}
