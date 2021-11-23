package org.atlanmod

import movies.MoviesPackage
import org.apache.spark.{SparkConf, SparkContext}
import org.atlanmod.findcouples.transformation.emf.serial.{MovieEMFStringConverter, SimpleIdentity}
import org.atlanmod.tl.engine.Utils.allTuplesByRule
import org.atlanmod.tl.model.impl.emf.serial.{SerializableEMFMetamodel, SerializableEMFModel, SerializableEObject}

object Main {

    final val pack = MoviesPackage.eINSTANCE
    final val factory = pack.getMoviesFactory

    val conf = new SparkConf()
    conf.setAppName("")
    conf.setMaster("local[1]")
    val sc = new SparkContext(conf)

    def main(args: Array[String]): Unit = {
        val root = factory.createRoot()
        val m1 = factory.createMovie()
        m1.setTitle("episode 1")
        val m2 = factory.createMovie()
        m2.setTitle("episode 2")
        val converter = MovieEMFStringConverter
        val sm = new SerializableEMFModel(root, converter)
        val mm = new SerializableEMFMetamodel("Movies", converter)
//        val o = TransformationSequential.execute(SimpleIdentity.identity_imdb(), sm, mm)
//        val oprime = TransformationParallel.execute(SimpleIdentity.identity_imdb(), sm, mm, 1, sc)

        val tr = SimpleIdentity.identity_imdb()
        val tuples: List[List[SerializableEObject]] = allTuplesByRule(tr, sm, mm)

//         sp => (matchPattern(tr, sm, mm, sp).flatMap(r => traceRuleOnPattern(r, sm, sp)))
//
//        val b = sc.parallelize(tuples.map(sp => matchPattern(tr, sm, mm, sp))).flatMap(r => traceRuleOnPattern(r, sm, sp)).collect
////        sc.parallelize(tuples).flatMap(tuple => tracePattern(tr, sm, mm, tuple)).collect()
////        val distr_tls: List[TraceLink[SerializableEObject, SerializableEObject]] = tuples.flatMap(tuple => tracePattern(tr, sm, mm, tuple))
    }

}
