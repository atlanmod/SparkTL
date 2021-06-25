package org.atlanmod.class2relational.model.transformation.dynamic

import org.atlanmod.class2relational.model.ModelSamples.{getClassModelSingle, getRelationalModelSample}
import org.atlanmod.class2relational.model.classmodel.{ClassElement, ClassLink, ClassMetamodel, ClassModel}
import org.atlanmod.class2relational.model.relationalmodel.{RelationalElement, RelationalLink, RelationalMetamodel, RelationalModel}
import org.atlanmod.class2relational.transformation.dynamic.{Class2Relational, Relational2Class}
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestRelational2ClassSeq extends AnyFunSuite {

    def makeRelationalModel: (List[DynamicElement], List[DynamicLink]) => RelationalModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]])

    def makeClassModel: (List[DynamicElement], List[DynamicLink]) => ClassModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new ClassModel(e.asInstanceOf[List[ClassElement]], l.asInstanceOf[List[ClassLink]])


    test("simple") {
        val class_model = getClassModelSingle
        val class_metamodel = ClassMetamodel.metamodel
        val relational_metamodel = RelationalMetamodel.metamodel
        val transformation_c2r = Class2Relational.class2relational()
        val transformation_r2c = Relational2Class.relational2class()
        val relational_model = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation_c2r,
            class_model, class_metamodel, makeModel = makeRelationalModel)
        val result = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation_r2c, relational_model,
            relational_metamodel, makeModel = makeClassModel)
        assert(result.equals(class_model))
    }

    test("simple equals to byrule") {
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.sequential.TransformationEngineByRule.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        assert(result_simple.equals(result_byrule))
    }

    test("simple equals totwophase") {
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        assert(result_simple.equals(result_byrule))
    }

    test("simple equals totwophaseHM") {
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val transformation = Relational2Class.relational2class()
        val result_simple = org.atlanmod.tl.engine.sequential.TransformationEngineImpl.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        val result_byrule = org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhaseHM.execute(transformation, model, metamodel,
            makeModel = makeClassModel)
        assert(result_simple.equals(result_byrule))
    }
}
