package org.atlanmod.class2relational.model.transformation.dynamic

import org.atlanmod.class2relational.model.ModelSamples.getRelationalModelSample
import org.atlanmod.class2relational.model.classmodel.{ClassElement, ClassLink, ClassModel}
import org.atlanmod.class2relational.model.relationalmodel.{RelationalElement, RelationalLink, RelationalMetamodel, RelationalModel}
import org.atlanmod.class2relational.transformation.dynamic.Relational2Class
import org.atlanmod.class2relational.transformation.dynamic.Relational2Class.isPivot_complex
import org.atlanmod.tl.model.impl.dynamic.{DynamicElement, DynamicLink}
import org.scalatest.funsuite.AnyFunSuite

class TestRelational2ClassSeq extends AnyFunSuite {

    def makeRelationalModel: (List[DynamicElement], List[DynamicLink]) => RelationalModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new RelationalModel(e.asInstanceOf[List[RelationalElement]], l.asInstanceOf[List[RelationalLink]])

    def makeClassModel: (List[DynamicElement], List[DynamicLink]) => ClassModel = (e: List[DynamicElement], l: List[DynamicLink])
    => new ClassModel(e.asInstanceOf[List[ClassElement]], l.asInstanceOf[List[ClassLink]])

    test("complex") {
        val model = getRelationalModelSample
        val metamodel = RelationalMetamodel.metamodel
        val r2c = Relational2Class.relational2class()
        val r2c_complex = Relational2Class.relational2class(foo_pivot = isPivot_complex)
        val res =  org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(r2c_complex,
            model, metamodel, makeModel = makeRelationalModel)
        val exp =  org.atlanmod.tl.engine.sequential.TransformationEngineTwoPhase.execute(r2c,
            model, metamodel, makeModel = makeRelationalModel)
        assert(res.equals(exp))
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
