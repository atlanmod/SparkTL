package org.atlanmod.transformation.dynamic

import org.apache.spark.SparkContext
import org.atlanmod.model.ModelSamples.{getClassModelSingle, getRelationalModelSample}
import org.atlanmod.model.classmodel.{ClassElement, ClassLink, ClassMetamodel, ClassModel}
import org.atlanmod.model.relationalmodel.{RelationalElement, RelationalLink, RelationalMetamodel, RelationalModel}
import org.atlanmod.model.{DynamicElement, DynamicLink}
import org.atlanmod.tl.util.SparkUtils
import org.scalatest.funsuite.AnyFunSuite

class TestRelational2ClassPar extends AnyFunSuite {



    def makeRelationalModel: (List[DynamicElement], List[DynamicLink]) => RelationalModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]])

    def makeClassModel: (List[DynamicElement], List[DynamicLink]) => ClassModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new ClassModel(e.asInstanceOf[List[ClassElement]], l.asInstanceOf[List[ClassLink]])


    test("simple in parallel") {
        val sc: SparkContext = SparkUtils.context(2)
        val class_model = getClassModelSingle
        val class_metamodel = ClassMetamodel.metamodel
        val relational_metamodel = RelationalMetamodel.metamodel
        val transformation_c2r = Class2Relational.class2relational()
        val transformation_r2c = Relational2Class.relational2class_simple()
        val relational_model = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation_c2r,
            class_model, class_metamodel, sc, makeModel = makeRelationalModel)
        val result = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation_r2c, relational_model,
            relational_metamodel, sc, makeModel = makeClassModel)
        sc.stop()
        assert(result.equals(class_model))
    }

    test("simple equals to byrule in parallel") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class_simple()
        val result_simple = org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.parallel.TransformationEngineByRule.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        sc.stop()
        assert(result_simple.equals(result_byrule))
    }

    test("simple equals to twophase in parallel") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class_simple()
        val result_simple = org.atlanmod.tl.engine.parallel.TransformationEngineImpl.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhase.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        sc.stop()
        assert(result_simple.equals(result_byrule))
    }

    test("simple equals to twophaseHM in parallel") {
        val sc: SparkContext = SparkUtils.context(2)
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class_simple()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhaseHM.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.parallel.TransformationEngineTwoPhaseHM.execute(transformation, model, metamodel,
            sc, makeModel = makeClassModel)
        sc.stop()
        assert(result_simple.equals(result_byrule))
    }
}
